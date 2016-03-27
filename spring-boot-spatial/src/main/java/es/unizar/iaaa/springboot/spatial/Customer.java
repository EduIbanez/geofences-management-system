package es.unizar.iaaa.springboot.spatial;

import com.vividsolutions.jts.geom.Geometry;
import groovy.transform.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@ToString(includeNames=true, includeFields = true)
public class Customer {

    @Id
    @GeneratedValue
    private long id;
    private String firstName;
    private String lastName;

    @Type(type="org.hibernate.spatial.GeometryType")
    private Geometry geom;

    public Customer() {
    }

    public Customer(String firstName, String lastName, Geometry geom) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.geom = geom;
    }

    public String toString() {
        return "Customer(id: "+id+" firsrtName: "+firstName+" lastName: "+lastName+" geom: "+geom+")";
    }
}