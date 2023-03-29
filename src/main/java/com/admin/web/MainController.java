package com.admin.web;

import com.admin.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@RequiredArgsConstructor
@Controller
public class MainController {
    private final MessageService messageService;

    @GetMapping("/dashboard")
    public String getDashBoardPage(Model model){
        RequestContextHolder.getRequestAttributes().setAttribute("messageTop5", messageService.findByTop5(), RequestAttributes.SCOPE_SESSION);
        return "main/dashboard";
    }
}
