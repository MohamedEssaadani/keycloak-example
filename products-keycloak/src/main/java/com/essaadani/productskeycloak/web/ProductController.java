package com.essaadani.productskeycloak.web;

import com.essaadani.productskeycloak.entities.Supplier;
import com.essaadani.productskeycloak.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductController{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;

    @Autowired
    private AdapterDeploymentContext adapterDeploymentContext;


    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){
        PagedModel<Supplier> pageSuppliers = keycloakRestTemplate.getForObject("http://localhost:8083/suppliers", PagedModel.class);
        model.addAttribute("suppliers", pageSuppliers);
        return "suppliers";
    }

    @GetMapping("/jwt")
    @ResponseBody
    public Map<String, String> map (HttpServletRequest request){
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = principal.getKeycloakSecurityContext();
        Map<String, String> map = new HashMap<>();
        map.put("access_token", keycloakSecurityContext.getTokenString());

        return map;
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e,Model model){
        model.addAttribute("errorMessage", "Probléme d'autorisation");
        return "errors";
    }

    @GetMapping("/profile")
    public String profile(RedirectAttributes attributes, HttpServletRequest request, HttpServletResponse response){
        HttpFacade httpFacade = new SimpleHttpFacade(request, response);
        KeycloakDeployment deployment = adapterDeploymentContext.resolveDeployment(httpFacade);
        attributes.addAttribute("referrer", deployment.getResourceName());
        attributes.addAttribute("referrer_uri", request.getHeader("referer"));
        return "redirect:"+deployment.getAccountUrl();

    }
}
