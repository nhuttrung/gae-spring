package vn.khtt.gae;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import com.googlecode.objectify.annotation.Id;

import java.io.IOException;

import javax.servlet.Filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.WordUtils;

import org.springframework.stereotype.Component;

@Component
public class CityCollectionFilter implements Filter {
  @Cache
  @Entity
  public static class City {
    @Id
    private String id;

    private String name;
    
    private String region;

    private String country;

    private City() {
    }

    public City(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public City(String id, String name, String region, String country) {
      this.id = id;
      this.name = name;
      this.region = region;
      this.country = country;
    }

    public String getId() {
      return id;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setRegion(String region) {
      this.region = region;
    }

    public String getRegion() {
      return region;
    }

    public void setCountry(String country) {
      this.country = country;
    }

    public String getCountry() {
      return country;
    }
  }
  
  public CityCollectionFilter() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    ObjectifyService.register(City.class);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)resp;
    
    String city = request.getHeader("X-AppEngine-City");
    String region = request.getHeader("X-AppEngine-Region");
    String country = request.getHeader("X-AppEngine-Country");
    String latLong = request.getHeader("X-AppEngine-CityLatLong");
    
    if (city != null){
      String id = city.replace(' ', '-');
      city = WordUtils.capitalizeFully(city);
      
      City c = ofy().load().key(Key.create(City.class, id)).now();
      if (c == null){
        c = new City(id, city, region, country);
        ofy().save().entity(c).now();
      }
    }
    
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }
}
