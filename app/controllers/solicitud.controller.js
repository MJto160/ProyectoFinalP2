import { Solicitud, Usuario } from "../models/index.models.js";
import bcrypt from "bcryptjs";

// Vendedor crea una solicitud
export const crearSolicitud = async (req, res) => {
  try {
    const { motivo } = req.body;
    const id_usuario = req.user.id_usuario; // viene del token

    if (!motivo) return res.status(400).json({ message: "Debe indicar un motivo" });

    const nueva = await Solicitud.create({ id_usuario, motivo });
    res.json({ message: "Solicitud enviada correctamente", nueva });
  } catch (err) {
    res.status(500).json({ message: "Error al crear solicitud", error: err.message });
  }
};

//Admin lista solicitudes
export const listarSolicitudes = async (req, res) => {
  try {
    const solicitudes = await Solicitud.findAll({
      include: [{ model: Usuario, attributes: ["nombre_usuario", "nombre_completo"] }],
      order: [["fecha_creacion", "DESC"]],
    });
    res.json(solicitudes);
  } catch (err) {
    res.status(500).json({ message: "Error al listar solicitudes", error: err.message });
  }
};

// Admin responde y cambia contraseña
export const responderSolicitud = async (req, res) => {
  try {
    const { id_solicitud } = req.params;
    const { nuevaContrasena, estado } = req.body; // estado = "resuelta" o "rechazada"

    const solicitud = await Solicitud.findByPk(id_solicitud);
    if (!solicitud) return res.status(404).json({ message: "Solicitud no encontrada" });

    if (estado === "rechazada") {
      await solicitud.update({ estado: "rechazada" });
      return res.json({ message: "Solicitud rechazada" });
    }

    // Cambiar contraseña del usuario
    const usuario = await Usuario.findByPk(solicitud.id_usuario);
    const hash = await bcrypt.hash(nuevaContrasena, 10);
    await usuario.update({ contrasena: hash });
    await solicitud.update({ estado: "resuelta" });

    res.json({ message: "Contraseña actualizada y solicitud resuelta" });
  } catch (err) {
    res.status(500).json({ message: "Error al responder", error: err.message });
  }
};
