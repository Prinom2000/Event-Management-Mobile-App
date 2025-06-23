package edu.ewubd.cse4892021260098;

public class Event {
    protected String eventId= "";
    protected String title= "";
    protected String vanue= "";
    protected long datetime= 0;
    long dateTime= System.currentTimeMillis();
    protected int numParticipation= 0;
    protected String description= "";

    public Event(String Id, String title, String vanue, long datetime, int numParticipation, String description){
        this.eventId=Id;
        this.datetime=datetime;
        this.vanue=vanue;
        this.numParticipation=numParticipation;
        this.description=description;
        this.title=title;
    }
    public String toString(){
        return this.title+", "+ this.description+", "+ this.eventId+", "+this.vanue+", "+this.datetime+", "+this.numParticipation;
    }
}
