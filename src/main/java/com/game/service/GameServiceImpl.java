package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GameServiceImpl implements GameService {
    private GameRepository gameRepository;

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> getPlayers(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                   Integer maxLevel, PlayerOrder order, int pageNumber, int pageSize) {
        return gameRepository.getPlayers(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer playerCount(String name, String title, Race race, Profession profession, Long after, Long before,
                               Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                               Integer maxLevel) {
        return gameRepository.playersCount(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel);
    }

    @Override
    public void create(Player player) {
        gameRepository.create(player);
    }

    @Override
    public boolean delete(Long id) {
        return gameRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Player getPlayer(Long id) {
        return gameRepository.getPlayer(id);
    }

    @Override
    public Player update(Long id, Player player) {
        return gameRepository.update(id, player);
    }
}
