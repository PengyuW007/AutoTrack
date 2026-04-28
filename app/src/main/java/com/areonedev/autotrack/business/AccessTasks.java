package com.areonedev.autotrack.business;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.objects.Task;

import java.util.ArrayList;
import java.util.List;
public class AccessTasks {
    private DataAccess dataAccess;
    private List<Task> tasks;
    private Task task;
    private int currTask;

    public AccessTasks(){
        this.dataAccess= Services.getDataAccess(Main.dbName);
        this.tasks = new ArrayList<>();
        this.task=null;
        this.currTask=0;
    }

    public String getTasks(List<Task>tasks){
        tasks.clear();
        return dataAccess.getTaskSequential(tasks);
    }

    public Task getSequential(){
        String result=null;

        if(task==null || tasks.isEmpty()){
            //the following line was added as a result of a failing test in AccessCoursesTest!
            tasks=new ArrayList<>();
            dataAccess.getTaskSequential(tasks);
            currTask=0; //Reset counter for a fresh list
        }
        if(currTask<tasks.size()){
            task=tasks.get(currTask);
            currTask++;
        }else {
            //now hit the end of the list, so reset the counter
            task=null;
            tasks=null;
        }

        return task;
    }

    public String insertTask(Task currTask){
        return dataAccess.insertTask(currTask);
    }

    public String updateTask(Task currTask){
        return dataAccess.updateTask(currTask);
    }

    public String deleteTask(Task currTask){
        return dataAccess.deleteTask(currTask);
    }
}
