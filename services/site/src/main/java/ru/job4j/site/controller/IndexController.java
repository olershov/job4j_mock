package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.service.*;


import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final ProfilesService profilesService;
    private final TopicsService topicsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            model.addAttribute("categories", categoriesService.getMostPopular());
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        var interviews = interviewsService.getByType(1);
        model.addAttribute("new_interviews", interviews);
        var submitters = interviews.stream()
                .map(InterviewDTO::getSubmitterId)
                .map(profilesService::getProfileById)
                .map(profile -> {
                    if (profile.isPresent()) {
                        return profile.get().getUsername();
                    }
                    return "username";
                })
                .collect(Collectors.toList());
        model.addAttribute("submitters", submitters);
        Map<Integer, Integer> interviewsCountInCategory = new HashMap<>();
        for (InterviewDTO interview : interviews) {
            int categoryId = topicsService.getById(interview.getTopicId()).getCategory().getId();
            Integer count = interviewsCountInCategory.get(categoryId);
            interviewsCountInCategory.put(categoryId, count == null ? 1 : ++count);
        }
        model.addAttribute("interviews_count", interviewsCountInCategory);
        return "index";
    }
}