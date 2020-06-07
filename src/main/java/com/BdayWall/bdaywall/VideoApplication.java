package com.BdayWall.bdaywall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kopal Choure
 */
@SpringBootApplication
public class VideoApplication
{
	/**
	 * Main spring boot application class.
	 *
	 * @param args The command line arguments.
	 */

	public static void main(String[] args) {

		SpringApplication.run(VideoApplication.class, args);
		System.out.println("started");
	}
}
