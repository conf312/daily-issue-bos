package com.admin.web;

import com.admin.domain.message.Message;
import com.admin.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@RequiredArgsConstructor
@RequestMapping("/message")
@Controller
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/list")
    public String getListPage(Model model, Message.Request request,
                              @RequestParam(required = false, defaultValue = "0") Integer page,
                              @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        RequestContextHolder.getRequestAttributes().setAttribute("messageTop5", messageService.findByTop5(), RequestAttributes.SCOPE_SESSION);
        model.addAttribute("resultMap", messageService.findAll(request, page, pageSize));
        return "message/list";
    }

    @PostMapping("/save")
    public String save(Model model, Message.Request request) {
        model.addAttribute("result", messageService.save(request));
        return "jsonView";
    }

    @PostMapping("/update/read-yn")
    public String updateReadYn(Model model, Message.Request request) {
        model.addAttribute("result", messageService.updateReadYn(request));
        return "jsonView";
    }

    @PostMapping("/update/use-yn")
    public String updateUseYn(Model model, Message.Request request) {
        model.addAttribute("result", messageService.updateUseYn(request));
        return "jsonView";
    }
}
