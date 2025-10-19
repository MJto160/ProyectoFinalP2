// app/controllers/reporte.controller.js
import { Venta, DetalleVenta, Partido, Usuario, Localidad } from "../models/index.models.js";
import { Sequelize } from "sequelize";

export const ventasPorPartido = async (req, res) => {
  try {
    const { id_partido } = req.params;
    const user = req.user; // viene del middleware de autenticación

    // Si es vendedor, solo puede ver sus ventas
    const where = user.rol === "vendedor" 
      ? { id_partido, "$Venta.id_vendedor$": user.id_usuario }
      : { id_partido };

    const ventas = await DetalleVenta.findAll({
      where,
      include: [Localidad, { model: Venta, include: [Usuario] }],
    });
    res.json(ventas);
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error en reporte por partido" });
  }
};

export const ventasPorVendedor = async (req, res) => {
  try {
    const { id_vendedor } = req.params;
    const user = req.user;

    // Solo el admin o el propio vendedor pueden ver esto
    if (user.rol !== "admin" && user.id_usuario != id_vendedor) {
      return res.status(403).json({ mensaje: "Acceso denegado" });
    }

    const ventas = await Venta.findAll({
      where: { id_vendedor },
      include: [Usuario],
    });
    res.json(ventas);
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error en reporte por vendedor" });
  }
};
