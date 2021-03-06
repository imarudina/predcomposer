package com.nosoftskills.predcomposer.rankings;

import com.nosoftskills.predcomposer.model.User;
import com.nosoftskills.predcomposer.prediction.PredictionsService;
import com.nosoftskills.predcomposer.user.UsersService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivan St. Ivanov
 */
@Named("rankingsViewer")
@ApplicationScoped
public class ViewRankingsBean {

    private List<Score> userScores = new ArrayList<>();

    @Inject
    private UsersService usersService;

    @Inject
    private PredictionsService predictionsService;

    @Inject
    private ScoreEvaluator scoreEvaluator;

    @PostConstruct
    public void loadUserScores() {
        calculateAllScores();
    }

    public String recalculate() {
        calculateAllScores();
        return "home";
    }

    private void calculateAllScores() {
        userScores = usersService.getAllUsers()
                .stream()
                .map(this::calculateScore)
                .sorted()
                .collect(Collectors.toList());
    }

    private Score calculateScore(User user) {
        return new Score(user, predictionsService.getPredictionsForUser(user)
                .stream()
                .mapToInt(scoreEvaluator::calculatePointsForPrediction)
                .sum());
    }

    public List<Score> getUserScores() {
        return userScores;
    }
}
