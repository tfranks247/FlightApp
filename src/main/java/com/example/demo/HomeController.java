package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    FlightRepository flightRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listFlights(Model model){
        model.addAttribute("flights", flightRepository.findAll());
        return "list";
    }

    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search")String search){
        model.addAttribute("flights", flightRepository.findByArrivingAirportContainingIgnoreCaseOrDepartingAirportContainingIgnoreCaseOrDateContainingIgnoreCaseOrFlightNumberContainingIgnoreCaseOrTypeContainingIgnoreCaseOrPriceContainingIgnoreCase(search, search, search, search, search, search));
        return "searchlist";
    }

    @GetMapping("/add")
    public String flightForm(Model model){
        model.addAttribute("flight", new Flight());
        return "flightform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Flight flight1, @Valid Flight flight, BindingResult result,
                              @RequestParam("file")MultipartFile file
                              ){
        if(result.hasErrors()){
            return "flightform";
        }
        if(file.isEmpty() && flight1.getImage() == null){
            return "redirect:/add";
        }
        if(!file.isEmpty()){
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                flight.setImage(uploadResult.get("url").toString());
                flightRepository.save(flight);

            } catch (IOException e){
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else
            flightRepository.save(flight);
        return "redirect:/";
    }

    @RequestMapping("/list/{id}")
    public String showFlight(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("flight", flightRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateFlight(@PathVariable("id") long id, Model model) {
        model.addAttribute("flight", flightRepository.findById(id).get());
        return "flightform";
    }
    @RequestMapping("/delete/{id}")
    public String delFlight(@PathVariable("id") long id) {
        flightRepository.deleteById(id);
        return "redirect:/";
    }

}
