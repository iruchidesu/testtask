package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping(value = "/rest/players", method = RequestMethod.GET)
    public ResponseEntity<List<Player>> getPlayers(String name, String title,
                                                   Race race,
                                                   Profession profession,
                                                   Long after, Long before,
                                                   Boolean banned,
                                                   Integer minExperience, Integer maxExperience,
                                                   Integer minLevel, Integer maxLevel,
                                                   @RequestParam(defaultValue = "ID") PlayerOrder order,
                                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                                   @RequestParam(defaultValue = "3") Integer pageSize) {
        final List<Player> players = gameService.getPlayers(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/players", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getName().length() > 12 || player.getTitle().length() > 30 || player.getName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getExperience() < 0 || player.getExperience() > 10_000_000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getBirthday().getTime() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        LocalDate birthday = player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (birthday.getYear() < 2000 || birthday.getYear() > 3000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        gameService.create(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/players/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> playersCount(String name, String title,
                                                Race race,
                                                Profession profession,
                                                Long after, Long before,
                                                Boolean banned,
                                                Integer minExperience, Integer maxExperience,
                                                Integer minLevel, Integer maxLevel) {
        Integer count = gameService.playerCount(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/players/{id}", method = RequestMethod.GET)
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        if (id == null || id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Player player = gameService.getPlayer(id);

        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/rest/players/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody Player player) {
        if (id == null || id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.OK);

        if (player.getName() != null && (player.getName().length() > 12 || player.getName().trim().isEmpty()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getTitle() != null && player.getTitle().length() > 30)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10_000_000))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getBirthday() != null) {
            if (player.getBirthday().getTime() < 0)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            LocalDate birthday = player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (birthday.getYear() < 2000 || birthday.getYear() > 3000)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Player playerUpdated = gameService.update(id, player);

        return playerUpdated != null
                ? new ResponseEntity<>(playerUpdated, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/rest/players/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        if (id == null || id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final boolean deleted = gameService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
