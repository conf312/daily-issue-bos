package com.admin.web;

import com.admin.domain.board.Board;
import com.admin.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/board-save")
    public String saveMember(Model model, Board.Request request, MultipartHttpServletRequest multiRequest) throws Exception {
        String msg = "Failed. Please try later.";

        Long result = boardService.saveBoard(request, multiRequest);

        if (result > 0) {
            msg = "Success Register.";
            model.addAttribute("url", "/board-list");
        }
        model.addAttribute("msg", msg);

        return "error/blank";
    }

    @GetMapping("/board-list")
    public String getBoardListPage(Model model,
                                   Board.Request request,
                                   @RequestParam(required = false, defaultValue = "0") Integer page,
                                   @RequestParam(required = false, defaultValue = "8") Integer pageSize) throws Exception {
        model.addAttribute("resultMap", boardService.findAll(request, page, pageSize));
        return "board/list";
    }

    @PostMapping("/board-update")
    public String updateBoard(Model model, Board.Request request, MultipartHttpServletRequest multiRequest) throws Exception {
        String msg = "Failed. Please try later.";

        Long result = boardService.updateBoard(request, multiRequest);

        if (result > 0) {
            msg = "Success Modified.";
            model.addAttribute("url", "board-list");
        }
        model.addAttribute("msg", msg);

        return "error/blank";
    }

    @GetMapping("/board-detail")
    public String getBoardDetailPage(Model model, @RequestParam(required = false, defaultValue = "0") Long id) throws Exception {
        if (id != 0) model.addAttribute("resultMap", boardService.findById(id));
        return "board/detail";
    }

    @PostMapping("/board-delete")
    public String deleteBoard(Model model, Board.Request request) throws Exception {

        String msg = "Failed. Please try later.";

        Long result = boardService.deleteBoard(request);

        if (result > 0) {
            msg = "Success Delete.";
            model.addAttribute("url", "board-list");
        }
        model.addAttribute("msg", msg);

        return "error/blank";
    }
}
