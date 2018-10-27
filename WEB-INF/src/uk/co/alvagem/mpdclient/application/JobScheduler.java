/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Uses cron4j to schedule javascript jobs
 * (file:///C:/usr/lib/cron4j-2.2.5/doc/manual-en.html) 
 * @author bruce.porteous
 *
 */
public class JobScheduler {

	private Application app;
	private Scheduler s;
	private ScriptEngine engine;
	private List<ScriptJob> jobs = new LinkedList<ScriptJob>();
	
	/**
	 * 
	 */
	public JobScheduler(Application app) {
		this.app = app;
		 s = new Scheduler();	
		 s.setDaemon(true);
		 
		 ScriptEngineManager engineManager = new ScriptEngineManager();
		 engine = engineManager.getEngineByName("nashorn");
		 engine.getContext().setAttribute("con", app.getConnection(), ScriptContext.ENGINE_SCOPE);
	}

	public void addJob(String schedule, String script){
		jobs.add(new ScriptJob(schedule, script));
	}
	
	public void removeJob(ScriptJob job) {
		jobs.remove(job);
		job.deschedule();
	}
	
	public List<ScriptJob> getJobs() {
		return Collections.unmodifiableList(jobs);
	}
	
	
	public class ScriptJob implements Runnable {
		
		private String schedule;
		private String script;
		private String id;
		
		
		
		private ScriptJob(String schedule, String script) {
			assert(schedule != null);
			assert(script != null);
			this.schedule = schedule;
			this.script = script;
			
			id = s.schedule(schedule,  this);
		}
		

		/**
		 * @return the schedule
		 */
		public String getSchedule() {
			return schedule;
		}

		/**
		 * @param schedule the schedule to set
		 */
		public void setSchedule(String schedule) {
			this.schedule = schedule;
			s.reschedule(id, schedule);
		}

		/**
		 * @return the script
		 */
		public String getScript() {
			return script;
		}

		/**
		 * @param script the script to set
		 */
		public void setScript(String script) {
			this.script = script;
		}

		private void deschedule(){
			s.deschedule(id);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				engine.eval(script);
			} catch (ScriptException e) {
				app.statusMessage(e.getMessage());
			}
		}
		
		
	}
}
