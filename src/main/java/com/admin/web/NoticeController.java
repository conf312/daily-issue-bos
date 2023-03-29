package com.admin.web;

import com.admin.domain.notice.Notice;
import com.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartRequest;

@RequiredArgsConstructor
@RequestMapping("/notice")
@Controller
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/list")
    public String getListPage(Model model, Notice.Request request,
                       @RequestParam(required = false, defaultValue = "0") Integer page,
                      @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", noticeService.findAll(request, page, pageSize));
        return "notice/list";
    }

    @GetMapping("/write")
    public String getWritePage(Model model, Notice.Request request) {
        if (request.getId() != null) {
            model.addAttribute("resultMap", noticeService.findById(request.getId()));
        }
        return "notice/write";
    }

    @PostMapping("/save")
    public String save(Model model, Notice.Request request, MultipartRequest multipartRequest) throws Exception {
        if (noticeService.save(request, multipartRequest) > 0) {
            model.addAttribute("url", "/notice/list");
            model.addAttribute("msg", "msg.register");
        }
        return "error/blank";
    }

    @PostMapping("/update")
    public String updateNotice(Model model, Notice.Request request, MultipartRequest multipartRequest) throws Exception {
        if (noticeService.updateNotice(request, multipartRequest) > 0) {
            model.addAttribute("url", "/notice/list");
            model.addAttribute("msg", "msg.modify");
        }
        return "error/blank";
    }

    @PostMapping("/delete")
    public String deleteNotice(Model model, Notice.Request request) {
        if (noticeService.deleteNotice(request) > 0) {
            model.addAttribute("url", "/notice/list");
            model.addAttribute("msg", "msg.delete");
        }
        return "error/blank";
    }
}
