/**
 * Created by imaculate on 12/18/13.
 */
 

import groovy.sql.Sql

import java.sql.*;

import groovy.lang.GroovyShell;
import com.jcraft.jsch.*

public class Project{
    long id;

    String name, grailsVersion,gitRepoSsh, destination, ownerEmail;







    static void addProject( email, name , grailsVersion, gitRepo, destination){
        def db = Sql.newInstance('jdbc:mysql://localhost:3306/Autodeployer', 'root', 'freedom', 'com.mysql.jdbc.Driver');
        db.execute("""Insert into Projects (Email, Name, GrailsVersion, gitRepo, Destination) values ($email, $name, $grailsVersion, $gitRepo, $destination)""" )
        db.close();

    }

    static void deleteProject( id){
        def db = Sql.newInstance('jdbc:mysql://localhost:3306/Autodeployer', 'root', 'freedom', 'com.mysql.jdbc.Driver');
        db.execute("""Delete from Projects where PID = $id""")


    }
    static void updateProject( id, name , grailsVersion, gitRepo, destination){
        def db = Sql.newInstance('jdbc:mysql://localhost:3306/Autodeployer', 'root', 'freedom', 'com.mysql.jdbc.Driver');
        db.execute("""update Projects set  Name = $name, GrailsVersion = $grailsVersion, gitRepo = $gitRepo, Destination = $destination where PID = $id""" )
        db.close();
    }

    static Collection<Object> getProjects(email){
        def db = Sql.newInstance('jdbc:mysql://localhost:3306/Autodeployer', 'root', 'freedom', 'com.mysql.jdbc.Driver');
        def projectList = [];
      

        
        // fetch each row
        db.eachRow("""Select * from Projects where Email = $email"""){row ->
            projectList << new Project(id: row[0], ownerEmail: row[1], name: row[2],grailsVersion: row[3],gitRepoSsh: row[4], destination: row[5] )
        }
        db.close();
        projectList
    }

	static boolean clone(git){
		def cloned = false
		def p = "git clone $git".execute()
		if (p.waitFor() == 0){ cloned = true }
		cloned
		
	}
	static boolean build(git){
		def built = false
		def name = git.substring(git.lastIndexOf("/")+1, git.lastIndexOf("."))
		def p = "sh -c cd $name; grails upgrade; grails war".execute()
		if (p.waitFor() == 0){ built = true }
		built

	}
	
	static boolean deploy(folder, destination){
		def deployed = false
		def p1 = "ssh destination"
		if(p1.waitFor() == 0){
			def p2 = "scp -r */target/*.war destination; exit".execute()
			if(p2.waitFor() == 0){deployed = true}
	
			def p3 = "rm -rf folder; rm -rf */*.war".execute()
		}
		deployed
	}	






    /*static void deploy(git, destination, version){
       	def m = "cd "/tmp"".execute()
	def user = "git config --global user.name "Firstname Lastname""
	def mail =  "git config --global user.email "your_email@youremail.com""
	"git clone git".execute()
	"cd "/$git"".execute()
	"grails war".execute() 
	def file = git - ".git" + "-01.war"
	ssh destination
	scp -r file destination 
	exit
	"rm -rf "/tmp/$git"".execute()
    }
*/

}
