package org.baderlab.csapps.socialnetwork;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Run all the Social Network App tests at once (for devs)
 * 
 * @author Victor Kofia
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={TestAuthor.class, TestCollaboration.class, TestIncites.class, TestInteraction.class, TestSearch.class})
public class AllTests {

  /**
   * Sets the environment
   */
  @BeforeClass public static void setUp() {}

}

