package entities;

import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


 public class SokobanDBManager extends Observable implements DBmanager{
	 private static SessionFactory factory;
	 private Configuration configuration;

	 /**
	  * This is the c'tor of SokobanDBManager.
	  */
	 public SokobanDBManager() {
		 Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		configuration = new Configuration();
		configuration.configure();
		factory = configuration.buildSessionFactory();
	}
/**
 * This function add a level to the levels database table.
 */
	 public void addLevel(String fName) {
			LevelsManager level = new LevelsManager(fName);
			Transaction tx = null;
			Session session = factory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(level);
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}

		}
	 /**
	  * This function add a user to the users database table.
	  */
		public void addUser(String fName) {
			UsersManager user = new UsersManager(fName);
			Transaction tx = null;
			Session session = factory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(user);
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		/**
		 * This function insert a score to the scores database table.
		 * @param name- this is an input of the user name.
		 * @param levelName-the level name to be saved.
		 * @param time-the time that the user finished the level.
		 * @param steps-counter of how much steps it takes the user to finish the level.
		 */
		private void addScore(String name, String levelName,int time, int steps) {
			ScoresManager emp = new ScoresManager(name,levelName,time,steps);
			Transaction tx = null;
			Session session = factory.openSession();
			try {
			tx = session.beginTransaction();
			session.save(emp);
			tx.commit();
			}
			catch (HibernateException e) {
			if (tx != null)
			tx.rollback();
			e.printStackTrace();
			} finally {
			session.close();
			}
		}
		/**
		 * This function delete a score from the scores database table.
		 * @param score-the score that need to be deleted.
		 */
		private void deleteScore(ScoresManager score) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
			tx = session.beginTransaction();
			session.delete(score);
			tx.commit();
			}
			catch (HibernateException e) {
			if (tx != null)
			tx.rollback();
			e.printStackTrace();
			}
			finally {
			session.close();
			}
			}

		/**
		 * This function checks if there is already a score in the data base.
		 * If there is, it checks if we need to save a new score or not.
		 */
		public void checkIfsave(String name,String levelName, int step, int time) {
			UsersManager user=null;
			ScoresManager scores=null;
			LevelsManager levels=null;

			Session session = factory.openSession();

			Query<UsersManager> query = session.createQuery("from Users E where E.fullname='"+name+"'");
			List<UsersManager> list = query.list();
			if(!list.isEmpty())
			{
				user=list.get(0);
			}
			else
			{
				addUser(name);
			}

			Query<LevelsManager> query2 = session.createQuery("from Levels E where E.levelname='"+levelName+"'");
			List<LevelsManager> list2 = query2.list();
			if (!list2.isEmpty())
			{
				levels=list2.get(0);
			}
			else
			{
				addLevel(levelName);
			}

			Query<ScoresManager> query3 = session.createQuery("from Scores s Where s.levelname='"+levelName+"' and s.fullname='"+name+"'");
			List<ScoresManager> list3 = query3.list();

			if (!list3.isEmpty())
			{
				scores=list3.get(0);
				if(scores.getSteps()>step||scores.getTime()>time)
				{
					deleteScore(scores);
					addScore(name,levelName, time, step);
				}

			}
			else
			{
				addScore(name, levelName, time, step);
			}

		}
		/**
		 * This function shows a specific scores for player.
		 * @param selectedScore- the score that the user click on.
		 * @return- return a table.
		 */
			public ObservableList<ScoresManager> getUser(ScoresManager selectedScore) {

				ObservableList<ScoresManager> users = FXCollections.observableArrayList();
				Query<ScoresManager> query = HibernateUtil.getSessionFactory().openSession().createQuery("from Scores S where S.fullname='" + selectedScore.getFullname() + "'");
				List<ScoresManager> list = query.list();

				for (ScoresManager u : list) {
					users.add(u);
				}
				for (ScoresManager user : users)
					user.toString();

				return users;
			}
		/**
		 * This function shows all scores.
		 * @return-return a table of the scores.
		 */
		public ObservableList<ScoresManager> getAllScores() {
			ObservableList<ScoresManager> users = FXCollections.observableArrayList();
				Query<ScoresManager> query = HibernateUtil.getSessionFactory().openSession().createQuery("from Scores");
				query.setMaxResults(10);
				List<ScoresManager> list = query.list();
				if (!list.isEmpty()) {

					for (ScoresManager u : list) {
						users.add(u);
					}
					for (ScoresManager user : users)
						user.toString();
					return users;
				}
				return users;
			}

		/**
		 * This function shows all scores from db.
		 * @param levelName- the name of the level that we want to see all the scores.
		 * @return- return a table.
		 */
		public ObservableList<ScoresManager> getUsers(String levelName) {
			ObservableList<ScoresManager> users = FXCollections.observableArrayList();
			if(levelName!=null)
			{
				Query<ScoresManager> query = HibernateUtil.getSessionFactory().openSession().createQuery("from Scores S where S.levelname='"+levelName+"'");
				query.setMaxResults(10);
				List<ScoresManager> list = query.list();
				if (!list.isEmpty()) {

					for (ScoresManager u : list) {
						users.add(u);
					}
					for (ScoresManager user : users)
						user.toString();
					return users;
				}
			}
					return users;
		}
 }
