package com.ruoyi.web.controller.quiz.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.KnowledgeAttachment;
import com.ruoyi.system.domain.dto.KnowledgeAttachmentGroupDTO;
import com.ruoyi.system.service.IKnowledgeAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识点附件后台管理（按知识点分组）
 */
@RestController
@RequestMapping("/quiz/knowledgeAttachment")
public class KnowledgeAttachmentAdminController extends BaseController {

    @Autowired
    private IKnowledgeAttachmentService knowledgeAttachmentService;

    /**
     * 按知识点分组统计（group by point_id）
     */
    @PreAuthorize("@ss.hasPermi('quiz:knowledgeAttachment:group')")
    @GetMapping("/group")
    public TableDataInfo group(@RequestParam(required = false) String title,
                               @RequestParam(required = false) Long subjectId)
    {
        startPage();
        List<KnowledgeAttachmentGroupDTO> list = knowledgeAttachmentService.selectAttachmentGroupList(title, subjectId);
        return getDataTable(list);
    }

    /**
     * 查询附件列表（可按pointId过滤）
     */
    @PreAuthorize("@ss.hasPermi('quiz:knowledgeAttachment:list')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgeAttachment query)
    {
        startPage();
        List<KnowledgeAttachment> list = knowledgeAttachmentService.selectKnowledgeAttachmentList(query);
        return getDataTable(list);
    }

    /**
     * 删除附件记录（仅删除绑定记录）
     */
    @PreAuthorize("@ss.hasPermi('quiz:knowledgeAttachment:remove')")
    @Log(title = "知识点附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{attachmentIds}")
    public AjaxResult remove(@PathVariable Long[] attachmentIds)
    {
        return toAjax(knowledgeAttachmentService.deleteKnowledgeAttachmentByIds(attachmentIds));
    }
}

