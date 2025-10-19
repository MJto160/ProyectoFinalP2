// app/controllers/localidad.controller.js
import { Localidad } from "../models/index.models.js";

export const crearLocalidad = async (req, res) => {
  try {
    const nueva = await Localidad.create(req.body);
    res.json(nueva);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al crear localidad" });
  }
};

export const listarLocalidades = async (req, res) => {
  try {
    const localidades = await Localidad.findAll();
    res.json(localidades);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar localidades" });
  }
};
