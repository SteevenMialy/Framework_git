package etu4112.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import etu4112.framework.annotation.Url;
import etu4112.framework.model.Mapping;
import etu4112.framework.util.Utilitaire;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    private List<Class<?>> classesAnnotees;

    private HashMap<String, Mapping> mappingUrl = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            System.out.println("[Framework] Sprint 2 - Remplissage du dictionnaire...");

            List<Class<?>> classes = Utilitaire.scanPackageForAnnotation("test.controller",
                    etu4112.framework.annotation.Monannotation.class);

            for (Class<?> clazz : classes) {
                Method[] methods = clazz.getDeclaredMethods();

                for (Method method : methods) {
                    // Si la méthode possède l'annotation @Url
                    if (method.isAnnotationPresent(Url.class)) {
                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value(); // Ex: "user-liste"

                        // On crée le Mapping associé
                        Mapping mapping = new Mapping(clazz.getName(), method.getName());

                        // On l'ajoute dans notre HashMap
                        this.mappingUrl.put(urlValue, mapping);
                        System.out.println("[Framework] Route associée : /" + urlValue + " -> " + clazz.getSimpleName()
                                + "." + method.getName() + "()");
                    }
                }
            }
            System.out.println("[Framework] Dictionnaire complété avec " + mappingUrl.size() + " route(s).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // 1. On récupère d'abord l'URL saisie dans le chemin de navigation (ex:
            // /user-liste)
            String pathInfo = request.getPathInfo();
            String urlSaisi = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "";

            // 2. On récupère l'éventuelle URL corrigée passée par paramètre (ex:
            // ?url=user-liste)
            String urlParam = request.getParameter("url");

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Sprint 1 - Debug complet</title></head>");
            out.println("<body style='font-family: sans-serif; margin: 20px;'>");

            out.println("<h2>--- Framework Sprint 0 ---</h2>");
            out.println("<p>URI complète : " + request.getRequestURI() + "</p>");
            out.println("<p>URL relative pour le futur routeur : " + request.getPathInfo() + "</p>");

            out.println("<hr>");

            out.println("<h2>--- Framework Sprint 1 (Scan) ---</h2>");
            out.println("<h3>Classes possédant l'annotation :</h3>");
            out.println("<ul>");
            if (this.classesAnnotees == null || this.classesAnnotees.isEmpty()) {
                out.println("<li style='color: red;'>Aucune classe annotée trouvée.</li>");
            }else{
                // On affiche les classes annotées
                for (String url : this.mappingUrl.keySet()) {
                    Mapping m = this.mappingUrl.get(url);
                    out.println("<li><strong>/" + url + "</strong> -> " + m.getClassName() + "." + m.getMethodName() + "()</li>");
                }
            }
             // ==========================================
              // SCÉNARIO 3 : L'utilisateur a corrigé son URL (via le paramètre ?url=...)
              // ==========================================
            if (urlParam != null && !urlParam.isEmpty()) {
                if (this.mappingUrl.containsKey(urlParam)) {
                    Mapping mapping = this.mappingUrl.get(urlParam);
                    out.println(
                            "<div style='border: 2px solid #0056b3; background: #e6f2ff; padding: 15px; border-radius: 5px;'>");
                    out.println("<h3 style='color: #0056b3; margin-top: 0;'>🎯 URL Corrigée Validée</h3>");
                    out.println("<p>L'URL demandée <strong>/" + urlParam + "</strong> correspond à :</p>");
                    out.println("<ul>");
                    out.println("<li><strong>Classe :</strong> " + mapping.getClassName() + "</li>");
                    out.println("<li><strong>Méthode associée :</strong> " + mapping.getMethodName() + "()</li>");
                    out.println("</ul>");
                    out.println("</div>");
                } else {
                    out.println("<p style='color: red;'>⚠️ L'URL de correction '" + urlParam
                            + "' n'est pas valide non plus.</p>");
                }
            }

            // ==========================================
            // SCÉNARIO 1 : L'URL dans la barre d'adresse est valide dès le début
            // ==========================================
            else if (!urlSaisi.isEmpty() && this.mappingUrl.containsKey(urlSaisi)) {
                out.println(
                        "<div style='border: 2px solid #28a745; background: #e8f5e9; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
                out.println(
                        "<h3 style='color: #28a745; margin-top: 0;'>🎉 URL Actuelle Validée : /" + urlSaisi + "</h3>");
                out.println("<p>Correspondance directe : <strong>" + this.mappingUrl.get(urlSaisi).getClassName() + "."
                        + this.mappingUrl.get(urlSaisi).getMethodName() + "()</strong></p>");
                out.println("</div>");

                // On affiche le tout (Toutes les URLs existantes)
                out.println("<h3>📋 Liste complète des routes du Framework :</h3>");
                out.println(
                        "<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%; text-align: left;'>");
                out.println(
                        "<tr style='background-color: #f2f2f2;'><th>URL (Route)</th><th>Classe de Contrôleur</th><th>Méthode Cible</th></tr>");
                for (String url : this.mappingUrl.keySet()) {
                    Mapping m = this.mappingUrl.get(url);
                    out.println("<tr>");
                    out.println("<td><strong>/" + url + "</strong></td>");
                    out.println("<td>" + m.getClassName() + "</td>");
                    out.println("<td>" + m.getMethodName() + "()</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }

            // ==========================================
            // SCÉNARIO 2 : L'URL est vide ou erronée
            // ==========================================
            else {
                out.println(
                        "<div style='border: 2px solid #dc3545; background: #fdf2f2; padding: 15px; border-radius: 5px;'>");
                if (urlSaisi.isEmpty()) {
                    out.println("<h3 style='color: #6c757d; margin-top: 0;'>Bienvenue ! Aucune URL spécifiée.</h3>");
                } else {
                    out.println(
                            "<h3 style='color: #dc3545; margin-top: 0;'>❌ URL '/" + urlSaisi + "' non trouvée.</h3>");
                }

                out.println("<p>Veuillez choisir ou corriger votre URL parmi les routes existantes :</p>");
                out.println("<ul>");
                for (String url : this.mappingUrl.keySet()) {
                    // Le lien transmet l'URL via le paramètre ?url= pour déclencher le Scénario 3
                    // au clic !
                    out.println("<li style='margin-bottom: 8px;'>");
                    out.println("<a href='" + request.getContextPath() + "/App/?url=" + url
                            + "' style='font-weight: bold; color: #0056b3;'>/" + url + "</a>");
                    out.println("</li>");
                }
                out.println("</ul>");
                out.println("</div>");
            }
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}