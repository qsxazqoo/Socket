package com.example.socket.controller;

import com.example.socket.service.MinaSocket;
import com.example.socket.service.SocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;


@Controller
public class SocketController extends HttpServlet {
    @Autowired
    SocketService socketService;
    @Autowired
    MinaSocket minaSocket;
    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @GetMapping("/")
    public String index(HttpSession session) {
        session.setAttribute("status", null);
        return "index";
    }

    @PostMapping("/connect")
    public String connect(Model model, HttpSession session) throws UnknownHostException {
        if(session.getAttribute("status")=="無法連線到主機"){
            return "redirect:/";
        }
        minaSocket.connect(session);
        return "index";
    }

    @PostMapping("/close")
    public String close(Model model, HttpSession session) throws IOException {
        minaSocket.close(session);
        return "index";
    }

    @PostMapping("/send")
    public String send(@RequestParam(value = "io", required = false) String io, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("status") != null && !io.isEmpty() && session.getAttribute("status")!="無法連線到主機") {
            logger.info("傳送: "+io);
            String recevied=minaSocket.send(io,session);
            session.setAttribute("display", recevied);
            logger.info("接收: "+recevied);
        }else if(io.isEmpty()) {
            model.addAttribute("fault", "  請輸入資料");
        }else{
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }

    @PostMapping("/transaction")
    public String transaction(@RequestParam(value = "transaction", required = false) String transaction, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("status") != null && session.getAttribute("status")!="無法連線到主機") {
                session.setAttribute("transaction", transaction);
                minaSocket.transaction(transaction);
                logger.info("交易代碼 : "+transaction);
        } else {
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }
}
