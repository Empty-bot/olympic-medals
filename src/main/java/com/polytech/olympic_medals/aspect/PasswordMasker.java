package com.polytech.olympic_medals.aspect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class PasswordMasker {

    // Noms de champs reconnus comme sensibles, indépendamment de la casse
    private static final List<String> CHAMPS_SENSIBLES = Arrays.asList(
            "password", "motdepasse", "mdp", "pwd"
    );

    private PasswordMasker() {
        // Classe utilitaire, instanciation interdite
    }

    /**
     * Cherche un champ "mot de passe" dans les arguments de la méthode
     * (typiquement un Request DTO) et retourne sa valeur masquée.
     * Retourne "N/A" si aucun champ de ce type n'est trouvé.
     */
    public static String extraireEtMasquer(Object[] arguments) {
        if (arguments == null) {
            return "N/A";
        }

        for (Object argument : arguments) {
            if (argument == null) {
                continue;
            }
            String motDePasse = chercherChampSensible(argument);
            if (motDePasse != null) {
                return masquer(motDePasse);
            }
        }
        return "N/A";
    }

    private static String chercherChampSensible(Object objet) {
        for (Field champ : objet.getClass().getDeclaredFields()) {
            if (CHAMPS_SENSIBLES.contains(champ.getName().toLowerCase())) {
                try {
                    champ.setAccessible(true);
                    Object valeur = champ.get(objet);
                    return valeur != null ? valeur.toString() : null;
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private static String masquer(String motDePasse) {
        if (motDePasse.isEmpty()) {
            return "****";
        }
        return "*".repeat(motDePasse.length());
    }
}