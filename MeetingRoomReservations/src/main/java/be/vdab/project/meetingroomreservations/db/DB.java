package be.vdab.project.meetingroomreservations.db;

public interface DB {

	public interface RESERVATIONS {
		public static final String TABLE = "reservations";
		public static final String ID = "_id";
		public static final String reservationId = "reservationId";
		public static final String meetingRoomId = "meetingRoomId"; //If this is based on the Java naming, shouldn't this be the object: meetingRoom
		public static final String beginDate = "beginDate"; // TODO: change String ot Date?
		public static final String endDate = "endDate";
		public static final String personName = "personName";
		public static final String description = "description";
        public static final String active = "active"; // todo: change to bool?

	}

    public interface MEETINGROOMS {
        public static final String ID = "_id";
        public static final String TABLE = "meetingRooms";
        public static final String meetingRoomId = "meetingRoomId";
        public static final String name = "name";

    }




}
