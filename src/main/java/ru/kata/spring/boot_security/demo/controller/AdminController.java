
package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;


import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@Valid
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    private PasswordEncoder passwordEncoder;
    private static final String REDIRECT = "redirect:/admin";

    @Autowired
    public AdminController(RoleService roleService, UserService userService, PasswordEncoder  passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "")
    public String getAllUsers(Principal principal, Model model) {
        System.out.println("Попали в метод getAllUsers");
        User user = userService.getUserByUsername(principal.getName());
        boolean hasAdminRole = false;
        for (Role role : user.getRoles()) {
            if (role.getRoleName().equals("ROLE_ADMIN")) {
                hasAdminRole = true;
                break;
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("noAdminRole", !hasAdminRole);
        System.out.println("hasAdminRole"+ hasAdminRole);
        System.out.println("noAdminRole"+ !hasAdminRole);
        return "admin-user-page";
    }

    @GetMapping(value = "/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        System.out.println("Попали в метод getUserById класса AdminController");
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @GetMapping(value = "/new")
    public String addUser(Model model) {
        System.out.println("Попали в метод addUser класса AdminController");
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "create";
    }

    @PostMapping(value = "/new")
    public String add(@ModelAttribute("user") User user, BindingResult bindingResult
            , Model model, @RequestParam List<Long> ids) {
        /*System.out.println("Попали в метод add класса AdminController");
        Set<Role> assignedRole = roleService.(ids);
        user.setRoles(assignedRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));*/
        userService.updateUser(user);
        return REDIRECT;
    }

    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        System.out.println("Попали в метод delete класса AdminController");
        userService.removeUser(id);
        return REDIRECT;
    }

    @GetMapping(value = "/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model) {
        System.out.println("Попали в метод updateUser класса AdminController");
        User user = userService.getUserById(id);
        List<Role> allRoles = roleService.getAllRoles();
        Set<Role> userRoles = user.getRoles();

        for (Role role : allRoles) {
            if (userRoles.contains(role)) {
                role.setActive(true);
            } else {
                role.setActive(false);
            }
        }

        System.out.println("assignedRole "+allRoles);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "edit";
    }

    @PatchMapping(value = "/edit")
    public String update(@ModelAttribute("user") User user, BindingResult bindingResult
            , Model model) {
        System.out.println("Попали в метод update класса AdminController");
        //Set<Role> assignedRole = roleService.findAllRoleId(ids);
        //user.setRoles(assignedRole);
        userService.updateUser(user);
        return REDIRECT;
    }
}