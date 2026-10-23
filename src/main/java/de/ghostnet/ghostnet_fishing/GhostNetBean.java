package de.ghostnet.ghostnet_fishing;

import java.io.Serializable;

import java.util.List;

import javax.ejb.Stateless;

import javax.inject.Named;

import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

@Named
@Stateless
public class GhostNetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "ghostnetPU")

    private EntityManager em;

    private GhostNet ghostNet = new GhostNet();

    private GhostNet ausgewaehltesNetz;

    private String meldendeName;

    private String meldendeTelefon;

    private String bergendeName;

    private String bergendeTelefon;

    private String verschollenName;

    private String verschollenTelefon;

    private boolean verschollenFehler;


    public GhostNet getGhostNet() {

        return ghostNet;

    }

    public void setGhostNet(GhostNet ghostNet) {

        this.ghostNet = ghostNet;

    }

    public GhostNet getAusgewaehltesNetz() {

        return ausgewaehltesNetz;

    }

    public void setAusgewaehltesNetz(GhostNet ausgewaehltesNetz) {

        this.ausgewaehltesNetz = ausgewaehltesNetz;

    }

    public String getMeldendeName() {

        return meldendeName;

    }

    public void setMeldendeName(String meldendeName) {

        this.meldendeName = meldendeName;

    }

    public String getMeldendeTelefon() {

        return meldendeTelefon;

    }

    public void setMeldendeTelefon(String meldendeTelefon) {

        this.meldendeTelefon = meldendeTelefon;

    }

    public String getBergendeName() {

        return bergendeName;

    }

    public void setBergendeName(String bergendeName) {

        this.bergendeName = bergendeName;

    }

    public String getBergendeTelefon() {

        return bergendeTelefon;

    }

    public void setBergendeTelefon(String bergendeTelefon) {

        this.bergendeTelefon = bergendeTelefon;

    }

    public String getVerschollenName() {

        return verschollenName;

    }

    public void setVerschollenName(String verschollenName) {

        this.verschollenName = verschollenName;

    }

    public String getVerschollenTelefon() {

        return verschollenTelefon;

    }

    public void setVerschollenTelefon(String verschollenTelefon) {

        this.verschollenTelefon = verschollenTelefon;

    }

    public boolean isVerschollenFehler() {

        return verschollenFehler;

    }

    public void setVerschollenFehler(boolean verschollenFehler) {

        this.verschollenFehler = verschollenFehler;

    }


    public String speichern() {

        ghostNet.setStatus(Status.GEMELDET);

        if (meldendeName != null && !meldendeName.trim().isEmpty()) {

            List<Person> personen = em.createQuery(

                    "SELECT p FROM Person p "
                    + "WHERE p.name = :name "
                    + "AND p.role = :role",

                    Person.class)

                    .setParameter("name", meldendeName)
                    .setParameter("role", Role.MELDENDE_PERSON)
                    .getResultList();

            Person person;

            if (personen.isEmpty()) {

                person = new Person();

                person.setName(meldendeName);

                person.setPhone(meldendeTelefon);

                person.setRole(Role.MELDENDE_PERSON);

                em.persist(person);

            } else {

                person = personen.get(0);

            }

            ghostNet.setReportedBy(person);

        }

        em.persist(ghostNet);

        ghostNet = new GhostNet();

        meldendeName = null;

        meldendeTelefon = null;

        return "index.xhtml?faces-redirect=true";

    }


    public List<GhostNet> getGemeldeteNetze() {

        return em.createQuery(

                "SELECT g FROM GhostNet g "
                + "WHERE g.status = :gemeldet "
                + "OR g.status = :bergung",

                GhostNet.class)

                .setParameter("gemeldet", Status.GEMELDET)
                .setParameter("bergung", Status.BERGUNG_BEVORSTEHEND)
                .getResultList();

    }


    public String bergungUebernehmen() {

        if (ausgewaehltesNetz == null) {

            return "bergung.xhtml?faces-redirect=true";

        }

        if (bergendeName == null || bergendeName.trim().isEmpty()

                || bergendeTelefon == null || bergendeTelefon.trim().isEmpty()) {

            return "bergung.xhtml?faces-redirect=true";

        }

        List<Person> personen = em.createQuery(

                "SELECT p FROM Person p "
                + "WHERE p.name = :name "
                + "AND p.phone = :phone "
                + "AND p.role = :role",

                Person.class)

                .setParameter("name", bergendeName)
                .setParameter("phone", bergendeTelefon)
                .setParameter("role", Role.BERGENDE_PERSON)
                .getResultList();

        Person person;

        if (personen.isEmpty()) {

            person = new Person();

            person.setName(bergendeName);

            person.setPhone(bergendeTelefon);

            person.setRole(Role.BERGENDE_PERSON);

            em.persist(person);

        } else {

            person = personen.get(0);

        }

        GhostNet netz = em.find(

                GhostNet.class,

                ausgewaehltesNetz.getId());

        netz.setAssignedTo(person);

        netz.setStatus(Status.BERGUNG_BEVORSTEHEND);

        em.merge(netz);

        bergendeName = null;

        bergendeTelefon = null;

        ausgewaehltesNetz = null;

        return "bergung.xhtml?faces-redirect=true";

    }


    public String alsGeborgenMelden() {

        if (ausgewaehltesNetz == null) {

            return "bergung.xhtml?faces-redirect=true";

        }

        GhostNet netz = em.find(

                GhostNet.class,

                ausgewaehltesNetz.getId());

        netz.setStatus(Status.GEBORGEN);

        em.merge(netz);

        ausgewaehltesNetz = null;

        return "bergung.xhtml?faces-redirect=true";

    }


    public String alsVerschollenMelden() {

        if (ausgewaehltesNetz == null) {

            return "bergung.xhtml?faces-redirect=true";

        }

        if (verschollenName == null || verschollenName.trim().isEmpty()

                || verschollenTelefon == null || verschollenTelefon.trim().isEmpty()) {

            verschollenFehler = true;

            return null;

        }

        verschollenFehler = false;

        List<Person> personen = em.createQuery(

                "SELECT p FROM Person p "
                + "WHERE p.name = :name "
                + "AND p.phone = :phone "
                + "AND p.role = :role",

                Person.class)

                .setParameter("name", verschollenName)
                .setParameter("phone", verschollenTelefon)
                .setParameter("role", Role.VERSCHOLLEN_MELDENDE_PERSON)
                .getResultList();

        Person person;

        if (personen.isEmpty()) {

            person = new Person();

            person.setName(verschollenName);

            person.setPhone(verschollenTelefon);

            person.setRole(Role.VERSCHOLLEN_MELDENDE_PERSON);

            em.persist(person);

        } else {

            person = personen.get(0);

        }

        GhostNet netz = em.find(

                GhostNet.class,

                ausgewaehltesNetz.getId());

        netz.setMissingReportedBy(person);

        netz.setStatus(Status.VERSCHOLLEN);

        em.merge(netz);

        verschollenName = null;

        verschollenTelefon = null;

        ausgewaehltesNetz = null;

        return "bergung.xhtml?faces-redirect=true";

    }


    public boolean kannBergungUebernehmen(GhostNet netz) {

        return netz.getStatus() == Status.GEMELDET;

    }


    public boolean kannGeborgenMelden(GhostNet netz) {

        return netz.getStatus() == Status.BERGUNG_BEVORSTEHEND;

    }


    public String getStatusText(GhostNet netz) {

        if (netz.getStatus() == Status.GEMELDET) {

            return "Gemeldet";

        }

        if (netz.getStatus() == Status.BERGUNG_BEVORSTEHEND) {

            return "Bergung bevorstehend";

        }

        if (netz.getStatus() == Status.GEBORGEN) {

            return "Geborgen";

        }

        if (netz.getStatus() == Status.VERSCHOLLEN) {

            return "Verschollen";

        }

        return "";

    }

}