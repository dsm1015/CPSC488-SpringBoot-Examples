package edu.sru.cpsc.webshopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import edu.sru.cpsc.webshopping.secure.TwoFactorAuthentication;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TwoFactorAuthentication twoFactorAuth;

  // Mapping for the /index URL when initiated through Tomcat
  @RequestMapping({"/index"})
  public String showUserList(Model model) {
    return "index";
  }

  @RequestMapping({"/"})
  public String showIndex(Model model) {
    return "index";
  }

  @RequestMapping({"/login"})
  public String showLoginPage() {
    return "login";
  }

  @RequestMapping({"/missionStatement"})
  public String showMission() {
    return "missionStatement";
  }

  @RequestMapping({"/FAQ"})
  public String showFAQ() {
    return "FAQ";
  }

  @PostMapping({"/verify2FA"})
  public String verify2FA(@RequestParam String code, HttpServletRequest request) {
    String username = (String) request.getSession().getAttribute("tempUsername");
    if (username == null) {
        return "redirect:/login";
    }

    User user = userRepository.findByUsername(username);
    if (twoFactorAuth.verify2FACode(user, code)) {
        request.getSession().removeAttribute("tempUsername"); // Clear the temporary attribute
        // Complete the authentication and redirect
        return "redirect:/homePage";
    }

    return "redirect:/verify2FAPage?error";
  }

  @RequestMapping({"/verify2FAPage"})
  public String showVerify2FAPage() {
    return "verify2FA";
  }
}
