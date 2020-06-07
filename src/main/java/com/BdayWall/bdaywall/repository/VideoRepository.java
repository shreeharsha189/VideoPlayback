package com.BdayWall.bdaywall.repository;

import com.BdayWall.bdaywall.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

    public interface VideoRepository extends MongoRepository<Video, Integer> {
        /**
         * Function to get the list of users bday according to the given teamname and month.
         *
         * @param teamname to get the team name of user.
         * @param months   to get the month.
         * @return list of bdays.
         */
        public List<Video> findByTeamnameAndMonthsIgnoreCase(String teamname, String months);

        /**
         * Function to get the list of people who's bday is on this current date,using the day and month.
         *
         * @param day    to give today's day.
         * @param months for the current month.
         * @return list of bdays.
         */
        public List<Video> findByDayAndMonthsIgnoreCase(Integer day, String months);

        public Video findByAssocid(String associd);
    }


