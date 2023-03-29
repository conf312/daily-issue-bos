package com.admin.web;

import com.admin.domain.timeboard.TimeBoard;
import com.admin.service.TimeBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartRequest;

@RequiredArgsConstructor
@RequestMapping("/time-board")
@Controller
public class TimeBoardController {
    private final TimeBoardService timeBoardService;

    @GetMapping("/list")
    public String getListPage(Model model, TimeBoard.Request request,
                              @RequestParam(required = false, defaultValue = "0") Integer page,
                              @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", timeBoardService.findAll(request, page, pageSize));
        return "time-board/list";
    }

    @GetMapping("/write")
    public String getWritePage(Model model, TimeBoard.Request request) {
        if (request.getId() != null) {
            model.addAttribute("info", timeBoardService.findById(request.getId()));
        }
        return "time-board/write";
    }

    @PostMapping("/save")
    public String save(Model model, TimeBoard.Request request, MultipartRequest multipartRequest) throws Exception {
        if (timeBoardService.save(request, multipartRequest) > 0) {
            model.addAttribute("url", "/time-board/list");
            model.addAttribute("msg", "msg.register");
        }
        return "error/blank";
    }

    @PostMapping("/update")
    public String updateTimeBoard(Model model, TimeBoard.Request request, MultipartRequest multipartRequest) throws Exception {
        if (timeBoardService.updateTimeBoard(request, multipartRequest) > 0) {
            model.addAttribute("url", "/time-board/list");
            model.addAttribute("msg", "msg.modify");
        }
        return "error/blank";
    }

    @PostMapping("/delete")
    public String deleteNotice(Model model, TimeBoard.Request request) {
        if (timeBoardService.deleteTimeBoard(request) > 0) {
            model.addAttribute("url", "/time-board/list");
            model.addAttribute("msg", "msg.delete");
        }
        return "error/blank";
    }
}
