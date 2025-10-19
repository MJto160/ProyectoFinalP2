// app/controllers/partido.controller.js
import { Partido } from "../models/index.models.js";

// Creaamos un partido
export const crearPartido = async (req, res) => {
  try {
    const nuevo = await Partido.create(req.body);
    res.json(nuevo);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al crear partido" });
  }
};

// Lista todos nuestros partidos
export const listarPartidos = async (req, res) => {
  try {
    const partidos = await Partido.findAll();
    res.json(partidos);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar partidos" });
  }
};
