import bcrypt from "bcryptjs";
import { Usuario } from "../models/index.models.js";

export const crearUsuario = async (req, res) => {
  try {
    const { nombre_usuario, contrasena, nombre_completo, rol } = req.body;

    const hash = await bcrypt.hash(contrasena, 10);

    const nuevo = await Usuario.create({
      nombre_usuario,
      contrasena: hash, 
      nombre_completo,
      rol,
    });

    res.json(nuevo);
  } catch (err) {
    res.status(500).json({ message: "Error al crear usuario", error: err.message });
  }
};

export const listarUsuarios = async (req, res) => {
  try {
    const usuarios = await Usuario.findAll();
    res.json(usuarios);
  } catch (err) {
    res.status(500).json({ message: "Error al listar usuarios" });
  }
};