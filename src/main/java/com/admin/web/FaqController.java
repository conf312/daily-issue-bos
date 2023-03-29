package com.admin.web;

import com.admin.domain.faq.Faq;
import com.admin.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/faq")
@Controller
public class FaqController {
    private final FaqService faqService;

    @GetMapping("/list")
    public String getListPage(Model model, Faq.Request request,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", faqService.findAll(request, page, pageSize));
        return "faq/list";
    }

    @GetMapping("/write")
    public String getWritePage(Model model, Faq.Request request) {
        if (request.getId() != null) {
            model.addAttribute("info", faqService.findById(request.getId()));
        }
        return "faq/write";
    }

    @PostMapping("/save")
    public String save(Model model, Faq.Request request) {
        if (faqService.save(request) > 0) {
            model.addAttribute("url", "/faq/list");
            model.addAttribute("msg", "msg.register");
        }
        return "error/blank";
    }

    @PostMapping("/update")
    public String updateFaq(Model model, Faq.Request request) {
        if (faqService.updateFaq(request) > 0) {
            model.addAttribute("url", "/faq/list");
            model.addAttribute("msg", "msg.modify");
        }
        return "error/blank";
    }

    @PostMapping("/delete")
    public String deleteFaq(Model model, Faq.Request request) {
        if (faqService.deleteFaq(request) > 0) {
            model.addAttribute("url", "/faq/list");
            model.addAttribute("msg", "msg.delete");
        }
        return "error/blank";
    }
}
