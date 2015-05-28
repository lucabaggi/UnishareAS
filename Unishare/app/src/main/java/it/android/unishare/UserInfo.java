package it.android.unishare;

/**
 * Created by luca on 28/05/15.
 */
public class UserInfo {

    private int userId;
    private String nickname;
    private int universityId;
    private String university;
    private int campusId;
    private String campus;
    private int specializationId;
    private String specialization;
    private int lastAccess;

    public UserInfo(Entity user){
        this.userId = Integer.parseInt(user.get("user_id"));
        this.nickname = user.get("nickname");
        this.universityId = Integer.parseInt(user.get("university_id"));
        this.university = user.get("university");
        this.campusId = Integer.parseInt(user.get("campus_id"));
        this.campus = user.get("campus");
        this.specializationId = Integer.parseInt(user.get("specialization_id"));
        this.specialization = user.get("specialization");
        this.lastAccess = Integer.parseInt(user.get("last_access"));
    }


    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getUniversityId() {
        return universityId;
    }

    public String getUniversity() {
        return university;
    }

    public int getCampusId() {
        return campusId;
    }

    public String getCampus() {
        return campus;
    }

    public int getSpecializationId() {
        return specializationId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getLastAccess() {
        return lastAccess;
    }


}
