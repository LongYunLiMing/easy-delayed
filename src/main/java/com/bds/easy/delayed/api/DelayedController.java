package com.bds.easy.delayed.api;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.Job;
import com.bds.easy.delayed.core.Scheduler;
import com.bds.easy.delayed.core.SchedulerException;
import com.bds.easy.delayed.store.DelayedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 10:50
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
@RestController
@RequestMapping("/delayed")
public class DelayedController{

    @Autowired
    private Scheduler scheduler;

    @PutMapping("/start")
    public ResponseEntity<Boolean> start() throws SchedulerException{
        this.scheduler.start();
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/pause")
    public ResponseEntity<Boolean> pause(){
        this.scheduler.pause();
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("resume")
    public ResponseEntity<Boolean> resume(){
        this.scheduler.resume();
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/shutdown")
    public ResponseEntity<Boolean> shutdown() throws SchedulerException{
        this.scheduler.shutdown();
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PostMapping("/schedule")
    public ResponseEntity<Boolean> schedule(@RequestParam("group") String group,
                                            @RequestParam("code") String code,
                                            @RequestParam("jobClass")String jobClass,
                                            @RequestParam("name")String name,
                                            @RequestParam("date")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws ClassNotFoundException, SchedulerException{
        Delayed delayed = new Delayed();
        delayed.setGroup(group);
        delayed.setCode(code);
        delayed.setDate(date);
        delayed.setName(name);
        delayed.setJobClass((Class<? extends Job>) Class.forName(jobClass));
        scheduler.scheduleJob(delayed);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PutMapping("/trigger/one")
    public ResponseEntity<Boolean> trigger(@RequestParam("group")String group,
                                           @RequestParam("code")String code) throws SchedulerException{
        this.scheduler.triggerJob(group, code);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/trigger/group")
    public ResponseEntity<Boolean> trigger(@RequestParam("group")String group) throws SchedulerException{
        this.scheduler.triggerJob(group);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @DeleteMapping("/one")
    public ResponseEntity<Boolean> delete(@RequestParam("group")String group,
                                          @RequestParam("code")String code) throws SchedulerException{
        this.scheduler.deleteJob(group, code);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @DeleteMapping("/group")
    public ResponseEntity<Boolean> delete(@RequestParam("group")String group) throws SchedulerException{
        this.scheduler.deleteJob(group);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/pause/one")
    public ResponseEntity<Boolean> pause(@RequestParam("group")String group,
                                         @RequestParam("code")String code) throws SchedulerException, DelayedException{
        this.scheduler.pauseJob(group, code);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/pause/group")
    public ResponseEntity<Boolean>pause(@RequestParam("group")String group) throws SchedulerException, DelayedException{
        this.scheduler.pauseJob(group);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/resume/one")
    public ResponseEntity<Boolean> resume(@RequestParam("group")String group,
                                          @RequestParam("code")String code) throws SchedulerException{
        this.scheduler.resumeJob(group, code);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PutMapping("/resume/group")
    public ResponseEntity<Boolean> resume(@RequestParam("group")String group) throws SchedulerException{
        this.scheduler.resumeJob(group);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
}
