package edu.nuos.lab4.controller;

import edu.nuos.lab4.entity.Car;
import edu.nuos.lab4.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(Model model) {
        if (!model.containsAttribute("car")) {
            Car defaultCar = new Car();
            defaultCar.setYear(2022);
            defaultCar.setMileage(35000);
            defaultCar.setPrice(17500.0);
            defaultCar.setInspectionDate(LocalDate.now());
            model.addAttribute("car", defaultCar);
        }
        model.addAttribute("cars", carService.findAll());
        model.addAttribute("editMode", false);
        return "cars";
    }

    @GetMapping("/edit/{id}")
    public String editCarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return carService.findById(id)
                .map(car -> {
                    model.addAttribute("car", car);
                    model.addAttribute("cars", carService.findAll());
                    model.addAttribute("editMode", true);
                    return "cars";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Автомобіль не знайдено!");
                    return "redirect:/cars";
                });
    }

    @PostMapping("/save")
    public String saveCar(@ModelAttribute("car") Car car, RedirectAttributes redirectAttributes) {
        boolean isUpdate = (car.getId() != null);
        carService.save(car);
        if (isUpdate) {
            redirectAttributes.addFlashAttribute("successMessage", "Дані автомобіля успішно оновлено!");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Автомобіль успішно додано до БД!");
        }
        return "redirect:/cars";
    }

    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carService.deleteById(id);
        redirectAttributes.addFlashAttribute("infoMessage", "Запис успішно видалено з БД!");
        return "redirect:/cars";
    }
}
