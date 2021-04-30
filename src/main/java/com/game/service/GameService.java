package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface GameService {
    List<Player> getPlayers(String name, String title, Race race, Profession profession, Long after, Long before,
                            Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                            Integer maxLevel, PlayerOrder order, int pageNumber, int pageSize);
    Integer playerCount(String name, String title, Race race, Profession profession, Long after, Long before,
                        Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                        Integer maxLevel);
    void create(Player player);
    boolean delete(Long id);
    Player getPlayer(Long id);

    Player update(Long id, Player player);
}
