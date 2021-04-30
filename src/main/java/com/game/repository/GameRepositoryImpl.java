package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Player_;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

@Repository
public class GameRepositoryImpl implements GameRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Player> getPlayers(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                   Integer maxLevel, PlayerOrder order, int pageNumber, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> criteriaQuery = cb.createQuery(Player.class);
        Root<Player> root = criteriaQuery.from(Player.class);
        criteriaQuery.select(root);

        Predicate criteria = getPredicate(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel, cb, root);

        criteriaQuery.where(criteria);
        criteriaQuery.orderBy(cb.asc(root.get(order.getFieldName())));
        Query query = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageSize * pageNumber)
                .setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public Integer playersCount(String name, String title, Race race, Profession profession, Long after, Long before,
                                Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                Integer maxLevel) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery(Player.class);
        Root<Player> root = criteriaQuery.from(Player.class);
        criteriaQuery.select(cb.count(root));

        Predicate criteria = getPredicate(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel, cb, root);

        criteriaQuery.where(criteria);
        Query query = entityManager.createQuery(criteriaQuery);
        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    private Predicate getPredicate(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                   Integer maxLevel, CriteriaBuilder cb, Root<Player> root) {
        Predicate criteria = cb.conjunction();

        if (name != null) {
            Predicate p = cb.like(root.get(Player_.name), "%" + name + "%");
            criteria = cb.and(criteria, p);
        }
        if (title != null) {
            Predicate p = cb.like(root.get(Player_.title), "%" + title + "%");
            criteria = cb.and(criteria, p);
        }
        if (race != null) {
            Predicate p = cb.equal(root.get(Player_.race), race);
            criteria = cb.and(criteria, p);
        }
        if (profession != null) {
            Predicate p = cb.equal(root.get(Player_.profession), profession);
            criteria = cb.and(criteria, p);
        }
        if (after != null) {
            Predicate p = cb.greaterThanOrEqualTo(root.get(Player_.birthday), new Date(after));
            criteria = cb.and(criteria, p);
        }
        if (before != null) {
            Predicate p = cb.lessThanOrEqualTo(root.get(Player_.birthday), new Date(before));
            criteria = cb.and(criteria, p);
        }
        if (banned != null) {
            Predicate p = cb.equal(root.get(Player_.banned), banned);
            criteria = cb.and(criteria, p);
        }
        if (minExperience != null) {
            Predicate p = cb.greaterThanOrEqualTo(root.get(Player_.experience), minExperience);
            criteria = cb.and(criteria, p);
        }
        if (maxExperience != null) {
            Predicate p = cb.lessThanOrEqualTo(root.get(Player_.experience), maxExperience);
            criteria = cb.and(criteria, p);
        }
        if (minLevel != null) {
            Predicate p = cb.greaterThanOrEqualTo(root.get(Player_.level), minLevel);
            criteria = cb.and(criteria, p);
        }
        if (maxLevel != null) {
            Predicate p = cb.lessThanOrEqualTo(root.get(Player_.level), maxLevel);
            criteria = cb.and(criteria, p);
        }
        return criteria;
    }

    @Override
    public void create(Player player) {
        player.calcLevel();
        player.calcUntilNextLevel();
        entityManager.persist(player);
    }

    @Override
    public boolean delete(Long id) {
        boolean deleted = false;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Player> criteriaDelete = cb.createCriteriaDelete(Player.class);
        Root<Player> root = criteriaDelete.from(Player.class);

        criteriaDelete.where(cb.equal(root.get("id"), id));
        int delEntities = entityManager.createQuery(criteriaDelete).executeUpdate();
        if (delEntities != 0)
            deleted = true;
        return deleted;
    }

    @Override
    public Player getPlayer(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> criteriaQuery = cb.createQuery(Player.class);
        Root<Player> root = criteriaQuery.from(Player.class);
        criteriaQuery.select(root);
        criteriaQuery.where(cb.equal(root.get("id"), id));
        Query q = entityManager.createQuery(criteriaQuery);
        try {
            return (Player) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Player update(Long id, Player player) {
        if (getPlayer(id) == null)
            return null;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Player> update = cb.createCriteriaUpdate(Player.class);
        Root<Player> root = update.from(Player.class);

        Player p = getPlayer(id);

        if (player.getName() != null) {
            p.setName(player.getName());
            update.set(root.get(Player_.name), p.getName());
        }

        if (player.getTitle() != null) {
            p.setTitle(player.getTitle());
            update.set(root.get(Player_.title), p.getTitle());
        }

        if (player.getRace() != null) {
            p.setRace(player.getRace());
            update.set(root.get(Player_.race), p.getRace());
        }

        if (player.getProfession() != null) {
            p.setProfession(player.getProfession());
            update.set(root.get(Player_.profession), p.getProfession());
        }

        if (player.getBirthday() != null) {
            p.setBirthday(player.getBirthday());
            update.set(root.get(Player_.birthday), p.getBirthday());
        }

        if (player.getBanned() != null) {
            p.setBanned(player.getBanned());
            update.set(root.get(Player_.banned), p.getBanned());
        }

        if (player.getExperience() != null) {
            p.setExperience(player.getExperience());
            p.calcLevel();
            p.calcUntilNextLevel();
            update.set(root.get(Player_.experience), p.getExperience());
            update.set(root.get(Player_.level), p.getLevel());
            update.set(root.get(Player_.untilNextLevel), p.getUntilNextLevel());
        }

        update.where(cb.equal(root.get("id"), id));
        entityManager.createQuery(update).executeUpdate();
        return p;
    }
}
