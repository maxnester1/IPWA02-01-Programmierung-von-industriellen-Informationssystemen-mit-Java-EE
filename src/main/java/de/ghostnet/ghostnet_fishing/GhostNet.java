package de.ghostnet.ghostnet_fishing;

import javax.persistence.*;

@Entity
@Table(name = "ghost_net")
public class GhostNet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double latitude;

    private double longitude;

    private String size;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = true)
    private Person reportedBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = true)
    private Person assignedTo;

    @ManyToOne
    @JoinColumn(name = "missing_reported_by", nullable = true)
    private Person missingReportedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Person getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Person reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Person getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Person assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Person getMissingReportedBy() {
        return missingReportedBy;
    }

    public void setMissingReportedBy(Person missingReportedBy) {
        this.missingReportedBy = missingReportedBy;
    }
}