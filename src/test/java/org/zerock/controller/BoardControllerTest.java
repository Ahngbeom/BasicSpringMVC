package org.zerock.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zerock.domain.BoardVO;
import org.zerock.service.BoardService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"file:web/WEB-INF/dispatcher-servlet.xml", "file:web/WEB-INF/securityContext.xml"})
@WebAppConfiguration
@RequiredArgsConstructor
class BoardControllerTest {

    private static final Logger logger = LogManager.getLogger();

    private final WebApplicationContext ctx;

    @InjectMocks
    @Autowired
    private BoardController controller;

    @Mock
    private BoardService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

        assertNotNull(logger);
        assertNotNull(controller);
//        assertNotNull(service);
        assertNotNull(mockMvc);
    }

    @Test
    void testBoardList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "1"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "5"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "10"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "12"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "15"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/list").
                        param("page", "20"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("pagination"));
    }

    @Test
    void testGetPosts() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/posts")
                .param("bno", String.valueOf(1)))
                .andExpect(status().isOk())
//                .andDo(print())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("post"));
    }

    @Test
    void testGetPosts(int bno) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/posts")
                        .param("bno", String.valueOf(bno)))
                .andExpect(status().isOk())
//                .andDo(print())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("post"));
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/register"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testRegistration() throws Exception {
        BoardVO board = new BoardVO("???????????????", "????????? ??????????????????.", "?????????", (String) null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/board/register")
                        .param("title", board.getTitle())
                        .param("content", board.getContent())
                        .param("writer", board.getWriter()))
                .andExpect(redirectedUrlPattern("/board/posts*"))
                .andExpect(status().is3xxRedirection())
//                .andDo(print())
                .andReturn();
        logger.info(mvcResult.getModelAndView());

        assertNotNull(board);
        assertNotNull(mvcResult);

        testGetPosts(Integer.parseInt(mvcResult.getModelAndView().getModel().get("result").toString()));
    }

    @Test
    void testModifyPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/board/modify")
                        .param("bno", String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testModification() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/board/modify")
                        .param("bno", String.valueOf(1))
//                        .requestAttr("bno", 1)
                        .param("title", "????????? ????????? ??????????????????.")
                        .param("content", "??? ??????????????????.")
                        .param("writer", "Ahngbeom"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/board/post*"))
                .andReturn();

        testGetPosts(1);
    }

    @Test
    void testRemoveAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/board/removeAll"))
                .andExpect(redirectedUrlPattern("/board/list*"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("result"));
        logger.info(mvcResult.getModelAndView().getModel().get("message"));
        testBoardList();
    }

    @Test
    void testRemove() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/board/remove")
                        .param("bno", String.valueOf(1)))
                .andExpect(redirectedUrlPattern("/board/list*"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        logger.info(mvcResult.getModelAndView().getModel().get("result"));
        logger.info(mvcResult.getModelAndView().getModel().get("message"));
        testBoardList();
    }
}