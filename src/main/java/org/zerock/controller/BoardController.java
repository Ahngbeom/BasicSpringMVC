package org.zerock.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardSearchVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.PageHeaderVO;
import org.zerock.domain.PaginationVO;
import org.zerock.security.CustomPasswordEncoder;
import org.zerock.service.BoardService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/")
public class BoardController {

    private static final Logger log = LogManager.getLogger();

    private final BoardService service;

    private static final PageHeaderVO pageHeader = new PageHeaderVO("Board", "/board/list", null);

    private static PaginationVO pagination = new PaginationVO();


    @GetMapping(value = {"/list", "/"})
    public ModelAndView boardList(HttpServletRequest request, ModelAndView mv, @RequestParam(value = "page", defaultValue = "1") int page) {
//        long postAmount = service.countBoard();

        List<BoardVO> boardListByPage = service.getBoardListWithPage(page);
        mv.addObject("pageHeader", pageHeader);
        mv.addObject("BoardList", boardListByPage);
//        mv.addObject("pageAmount", postAmount % 10 == 0 ? postAmount / 10 : postAmount / 10 + 1);
//        if (page <= 10)
//            mv.addObject("pageAmount", 10);
//        else
//            mv.addObject("pageAmount", ((page / 10) + 1) * 10 <= postAmount / 10 ? ((page / 10) + 1) * 10 : postAmount / 10 + 1);
        pagination.setPostsAmount(service.countBoard());
        pagination.setPageAmount(service.countBoard() / 10 + 1);
        pagination.setPageMin(page % 10 == 0 ? page - 9 : page / 10 * 10 + 1);
        pagination.setPageMax(
                page % 10 > 0
                ? (page + 10) / 10 * 10 < pagination.getPageAmount() ? (page + 10) / 10 * 10 : pagination.getPageAmount()
                : page < pagination.getPageAmount() ? page : pagination.getPageAmount());
//        log.warn(pagination);
        mv.addObject("pagination", pagination);
        mv.setViewName("board/list");
        return mv;
    }


    @GetMapping("/posts")
    public ModelAndView getPosts(RedirectAttributes redirectAttributes, ModelAndView mv, long bno) {
        mv.addObject("pageHeader", pageHeader);
        redirectAttributes.addFlashAttribute("boardAlertType", "Board Read");
        try {
            if (service.getBoardByBno(bno) == null)
                throw new NullPointerException("Invalid Board Post.");
            redirectAttributes.addFlashAttribute("boardAlertStatus", "SUCCESS");
            log.warn(service.getBoardByBno(bno));
            mv.addObject("Posts", service.getBoardByBno(bno));
            mv.setViewName("board/posts");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("boardAlertStatus", "FAILURE");
            mv.setViewName("redirect:/error");
        }
        return mv;
    }

    @GetMapping("/register")
    public ModelAndView register(ModelAndView mv) {
        mv.addObject("pageHeader", pageHeader);
        mv.setViewName("board/register");
        return mv;
    }

    @PostMapping("/register")
    public ModelAndView register(RedirectAttributes redirectAttributes, ModelAndView mv, BoardVO board, Principal principal) {
        board.setUserId(principal != null ? principal.getName() : null);
        redirectAttributes.addFlashAttribute("boardAlertType", "Board Registration");
        try {
            if (service.registerBoard(board) != 1) {
                throw new Exception("Registration Failed");
            }
            redirectAttributes.addFlashAttribute("boardAlertStatus", "SUCCESS");
            redirectAttributes.addAttribute("bno", board.getBno());
            mv.setViewName("redirect:/board/posts");
        } catch (Exception e) {
            log.error(e);
            redirectAttributes.addAttribute("boardAlertStatus", "FAILURE");
            mv.setViewName("redirect:/board/register");
        }
        return mv;
    }

    @GetMapping("/modify")
    public ModelAndView modifyPost(ModelAndView mv, int bno) {
        mv.addObject("pageHeader", pageHeader);
        mv.addObject("Post", service.getBoardByBno(bno));
        mv.setViewName("board/modify");
        return mv;
    }

    @PostMapping("/modify")
    public ModelAndView modifyPost(RedirectAttributes redirectAttributes, ModelAndView mv, BoardVO board) {
        redirectAttributes.addFlashAttribute("boardAlertType", "Board Modify");
        try {
            if (service.modifyBoard(board) == 0)
                throw new Exception("Modify Board Failed.");
            redirectAttributes.addFlashAttribute("boardAlertStatus", "SUCCESS");
            redirectAttributes.addAttribute("bno", board.getBno());
            mv.setViewName("redirect:/board/posts");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("boardAlertStatus", "FAILURE");
            mv.setViewName("redirect:/board/modify");
        }
        return mv;
    }

    @PostMapping("/removeAll") // ????????? ??????
    public ModelAndView removeAllPost(RedirectAttributes redirectAttributes, ModelAndView mv) {
        String message;
        redirectAttributes.addFlashAttribute("boardAlertType", "Board Remove ALL");
        try {
            if (service.countBoard() <= 0) {
                redirectAttributes.addFlashAttribute("boardAlertStatus", "WARNING");
                message = "Board is Empty.";
            } else {
                if (service.removeAllBoard() == 0)
                    throw new Exception("Board All Remove Failed");
                if (service.initBnoValue() == 0)
                    throw new Exception("Initialize bno value of Board Failed");
                message = "Board Remove All Success.";
                redirectAttributes.addFlashAttribute("boardAlertStatus", "SUCCESS");
            }
            log.info(message);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("boardAlertStatus", "FAILURE");
        }
        mv.setViewName("redirect:/board/list");
        return (mv);
    }

    @PostMapping("/remove")
    public ModelAndView removePost(RedirectAttributes redirectAttributes, ModelAndView mv, int bno) {
        redirectAttributes.addFlashAttribute("boardAlertType", "Board Remove");
        try {
            if (service.removeBoard(bno) == 0)
                throw new Exception("Board Remove Failed");
            if (service.countBoard() == 0) { // ????????? ????????? ????????? ?????? ???????????? ????????? ?????? bno??? ????????? ???????????????. ???????????? ???????????? ????????? ?????? ?????? ?????????
                if (service.initBnoValue() == 0)
                    throw new Exception("Initialize bno value of Board Failed");
            }
            redirectAttributes.addFlashAttribute("boardAlertStatus", "SUCCESS");
            mv.setViewName("redirect:/board/list");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("boardAlertStatus", "FAILURE");
            redirectAttributes.addAttribute("bno", bno);
            mv.setViewName("board/posts");
        }
        return (mv);
    }

    @PostMapping("/createDummy")
    public ModelAndView createDummy(ModelAndView mv, long dummyAmount) {
        BoardVO board = new BoardVO("Test", "Test", "Administrator", null);
        long limit = service.countBoard() + dummyAmount;
        long number = service.countBoard();
        while (number < limit) {
            log.info("Create Dummy : " + number);
            board.setTitle("Test" + number);
            board.setContent("Test" + number);
            service.registerBoard(board);
            number = service.countBoard();
        }
        mv.setViewName("redirect:/board/list");
        return mv;
    }

    @GetMapping("/search")
    public ModelAndView searchPost(ModelAndView mv, BoardSearchVO searchVO, @RequestParam(value = "page", defaultValue = "1") int page) {
        mv.addObject("pageHeader", pageHeader);
        List<BoardVO> searchResult = service.searchBoardByKeyword(searchVO);
        int size = searchResult.size();
        List<BoardVO> specPagesResult = new ArrayList<>(searchResult.subList(page * 10 - 10, Math.min(size, page * 10)));
        mv.addObject("BoardList", specPagesResult);
        mv.addObject("pageAmount", size % 10 == 0 ? size / 10 : size / 10 + 1);
        String checkedType = searchVO.isCheckTitle() ? "??????, " : "";
        checkedType += searchVO.isCheckContent() ? "??????, " : "";
        checkedType += searchVO.isCheckWriter() ? "?????????, " : "";
        checkedType = checkedType.substring(0, checkedType.length() - 2);
//        log.info("Requested Search Type: " + checkedType);
        if (size > 0) {
            mv.addObject("boardAlertType", "Board Search");
            mv.addObject("boardAlertStatus", "SUCCESS");
            mv.addObject("boardAlertMessage", "[" + checkedType + "] \"" + searchVO.getKeyword() + "\"??? ?????? " + size + "?????? ?????? ?????? ?????????.");
        } else {
            mv.addObject("boardAlertType", "Board Search");
            mv.addObject("boardAlertStatus", "FAILURE");
            mv.addObject("boardAlertMessage", "????????? ???????????? ???????????? ????????????.");
        }
        mv.setViewName("board/list");
        return mv;
    }
}
