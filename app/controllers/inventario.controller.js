// app/controllers/inventario.controller.js
import { Inventario, Partido, Localidad } from "../models/index.models.js";

// Creamos un inventario
export const crearInventario = async (req, res) => {
  try {
    const nuevo = await Inventario.create(req.body);
    res.json(nuevo);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al crear inventario" });
  }
};

// Listamos el inventario con detalles de partido y localidad
export const listarInventario = async (req, res) => {
  try {
    const inventario = await Inventario.findAll({
      include: [Partido, Localidad],
    });
    res.json(inventario);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar inventario" });
  }
};
