package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Forms.AddMenuItemForm;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.PreUpdate;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    private String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    private String add(Model model) {

        model.addAttribute("title", "Add menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method=RequestMethod.POST)
    private String add(Model model, @ModelAttribute @Valid Menu menu,
                       Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add menu");
            model.addAttribute(new Menu());
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    private String viewMenu(Model model, @PathVariable int id) {

        model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", menuDao.findOne(id).getName());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    private String additem(Model model, @PathVariable int id) {

        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());


        model.addAttribute("title", "Add Item to menu: " + menuDao.findOne(id).getName());
        model.addAttribute("form", form);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    private String additem(Model model, @Valid AddMenuItemForm form, Errors errors){

        if(errors.hasErrors()) {
            model.addAttribute("title", "Add Item to menu:{name}");
            model.addAttribute("form", new AddMenuItemForm(menuDao.findOne(form.getMenuId()),
                    cheeseDao.findAll()));
            return "menu/add-item";
        }

        System.out.println(form);

        Menu theMenu = menuDao.findOne(form.getMenuId());
        System.out.println(theMenu);
        Cheese theCheese = cheeseDao.findOne((form.getCheeseId()));
        System.out.println(theCheese);
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:/menu/view/" + form.getMenuId();
    }
}
