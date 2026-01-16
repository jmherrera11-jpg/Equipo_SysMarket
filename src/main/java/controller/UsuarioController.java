package controller;

import model.Usuario;
import repository.UsuarioRepository;

public class UsuarioController {
    private UsuarioRepository usuarioRepository;
    
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        
        Usuario usuario = usuarioRepository.autenticar(username, password);
        
        if (usuario == null) {
            throw new SecurityException("Usuario o contraseña incorrectos");
        }
        
        return usuario;
    }
    
    public Usuario buscarUsuario(String username) {
        return usuarioRepository.buscarPorUsername(username);
    }
}
