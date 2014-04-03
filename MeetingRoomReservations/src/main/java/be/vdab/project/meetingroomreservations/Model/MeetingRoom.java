package be.vdab.project.meetingroomreservations.Model;

/**
 * Created by jeansmits on 02/04/14.
 */
public class MeetingRoom {

    private String id;
    //private String meetingRoomId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  /*  public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }*/
}
