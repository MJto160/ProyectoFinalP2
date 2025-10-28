import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { Usuario } from "../models/index.models.js";

const SECRET = "your_jwt_secret_key";

// SIGNUP - Sin necesidad de token
export const signup = async (req, res) => {
  try {
    const { nombre_usuario, contrasena, nombre_completo, rol } = req.body;

    const usuarioExistente = await Usuario.findOne({ where: { nombre_usuario } });
    if (usuarioExistente) {
      return res.status(400).json({ mensaje: "El nombre de usuario ya existe" });
    }

    const hash = await bcrypt.hash(contrasena, 10);

    const nuevoUsuario = await Usuario.create({
      nombre_usuario,
      contrasena: hash,  // ← Usa 'contraseña' no 'contraseñ_hash'
      nombre_completo,
      rol: rol || "vendedor"
    });

    res.status(201).json({ 
      mensaje: "Usuario creado exitosamente",
      usuario: {
        id_usuario: nuevoUsuario.id_usuario,
        nombre_usuario: nuevoUsuario.nombre_usuario,
        nombre_completo: nuevoUsuario.nombre_completo,
        rol: nuevoUsuario.rol
      }
    });
  } catch (err) {
    res.status(500).json({ mensaje: "Error al crear usuario", error: err.message });
  }
};

// LOGIN - CON VERIFICACIÓN DE ESTADO
export const login = async (req, res) => {
  try {
    const { nombre_usuario, contrasena } = req.body;

    const usuario = await Usuario.findOne({ where: { nombre_usuario } });
    if (!usuario)
      return res.status(404).json({ mensaje: "Usuario no encontrado" });

    // ✅ NUEVO: Verificar si el usuario está activo
    if (!usuario.estado) {
      return res.status(401).json({ 
        mensaje: "Usuario desactivado. Contacte al administrador." 
      });
    }

    const passwordValida = await bcrypt.compare(contrasena, usuario.contrasena);
    if (!passwordValida)
      return res.status(401).json({ mensaje: "Contraseña incorrecta" });

    const token = jwt.sign(
      {
        id_usuario: usuario.id_usuario,
        rol: usuario.rol,
        estado: usuario.estado // ✅ Incluir estado en el token
      },
      SECRET,
      { expiresIn: "8h" }
    );

    res.json({
      token,
      rol: usuario.rol,
      nombre_completo: usuario.nombre_completo,
      estado: usuario.estado // ✅ Incluir estado en la respuesta
    });
  } catch (err) {
    res.status(500).json({ mensaje: "Error en el login", error: err.message });
  }
};