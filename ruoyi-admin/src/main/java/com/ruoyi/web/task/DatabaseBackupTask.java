package com.ruoyi.web.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定时数据库备份任务：每天凌晨将库结构+数据导出到 sql/all/stuctNdata/yyyy-MM-dd/ 下。
 *
 * @author ruoyi
 */
@Component
@ConditionalOnProperty(name = "backup.enabled", havingValue = "true")
public class DatabaseBackupTask {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackupTask.class);

    private static final Pattern JDBC_URL_PATTERN = Pattern.compile(
            "jdbc:mysql://([^:/]+):(\\d+)/([^?]+)"
    );

    @Value("${backup.basePath:daming-admin/sql/all/stuctNdata}")
    private String basePath;

    @Value("${spring.datasource.druid.master.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.druid.master.username}")
    private String username;

    @Value("${spring.datasource.druid.master.password}")
    private String password;

    /**
     * 每天凌晨 0 点执行备份，cron 可通过 backup.cron 覆盖。
     */
    @Scheduled(cron = "${backup.cron:0 0 0 * * ?}")
    public void backupDatabase() {
        String dateDir = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Path backupDir = resolveBackupDir(dateDir);
        String dbName = parseDatabaseName(jdbcUrl);
        if (dbName == null) {
            log.warn("[数据库备份] 无法从 JDBC URL 解析库名: {}", jdbcUrl);
            return;
        }
        File outputFile = backupDir.resolve(dbName + ".sql").toFile();
        try {
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            log.error("[数据库备份] 创建备份目录失败: {}", backupDir, e);
            return;
        }
        String host = parseHost(jdbcUrl);
        String port = parsePort(jdbcUrl);
        if (host == null || port == null) {
            log.warn("[数据库备份] 无法从 JDBC URL 解析 host/port: {}", jdbcUrl);
            return;
        }
        int exitCode = runMysqldump(host, port, username, password, dbName, outputFile);
        if (exitCode == 0) {
            log.info("[数据库备份] 成功: {}", outputFile.getAbsolutePath());
        } else {
            log.error("[数据库备份] 失败, exitCode={}, 请确认本机已安装 MySQL 客户端且 mysqldump 在 PATH 中", exitCode);
        }
    }

    private Path resolveBackupDir(String dateDir) {
        Path base = Paths.get(basePath).normalize();
        if (!base.isAbsolute()) {
            base = Paths.get(System.getProperty("user.dir")).resolve(base);
        }
        return base.resolve(dateDir);
    }

    private String parseDatabaseName(String url) {
        Matcher m = JDBC_URL_PATTERN.matcher(url);
        return m.find() ? m.group(3) : null;
    }

    private String parseHost(String url) {
        Matcher m = JDBC_URL_PATTERN.matcher(url);
        return m.find() ? m.group(1) : null;
    }

    private String parsePort(String url) {
        Matcher m = JDBC_URL_PATTERN.matcher(url);
        return m.find() ? m.group(2) : null;
    }

    /**
     * 调用 mysqldump 导出结构+数据到指定文件。
     * 需本机已安装 MySQL 客户端（mysqldump 在 PATH 中）。
     */
    private int runMysqldump(String host, String port, String user, String pwd, String database, File outputFile) {
        List<String> command = new ArrayList<>();
        command.add("mysqldump");
        command.add("-h");
        command.add(host);
        command.add("-P");
        command.add(port);
        command.add("-u");
        command.add(user);
        command.add("--password=" + pwd);
        command.add("--single-transaction");
        command.add("--routines");
        command.add("--triggers");
        command.add("--set-charset");
        command.add("--default-character-set=utf8mb4");
        command.add(database);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.redirectOutput(outputFile);

        try {
            Process process = pb.start();
            return process.waitFor();
        } catch (IOException e) {
            log.error("[数据库备份] 执行 mysqldump 异常", e);
            return -1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[数据库备份] 被中断", e);
            return -1;
        }
    }
}
