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

// Lista todos nuestros partidos CON ESTADOS AUTOMÁTICOS
export const listarPartidos = async (req, res) => {
  try {
    const partidos = await Partido.findAll();
    
    // Usar hora UTC para comparar correctamente
    const ahoraUTC = new Date().toISOString();
    
    for (let partido of partidos) {
      const fechaPartidoUTC = new Date(partido.fecha_partido).toISOString();
      let nuevoEstado = partido.estado;
      
      // Calcular fecha de finalización (3 horas después del partido)
      const fechaFinalizacion = new Date(new Date(partido.fecha_partido).getTime() + 3 * 60 * 60 * 1000).toISOString();
      
      console.log('ANÁLISIS:', {
        partido: `${partido.equipo_local} vs ${partido.equipo_visitante}`,
        fechaPartido: fechaPartidoUTC,
        fechaFinalizacion: fechaFinalizacion,
        ahora: ahoraUTC
      });
      
      // FLUJO AUTOMÁTICO:
      // 1. Si ya pasó la fecha + 3 horas → FINALIZADO
      if (ahoraUTC >= fechaFinalizacion && partido.estado !== 'finalizado') {
        nuevoEstado = 'finalizado';
        await partido.update({ estado: nuevoEstado });
        console.log(` FINALIZADO: ${partido.equipo_local} vs ${partido.equipo_visitante}`);
      }
      // 2. Si ya pasó la fecha pero no han pasado 3 horas → ACTIVO  
      else if (ahoraUTC >= fechaPartidoUTC && partido.estado === 'programado') {
        nuevoEstado = 'activo';
        await partido.update({ estado: nuevoEstado });
        console.log(` ACTIVO: ${partido.equipo_local} vs ${partido.equipo_visitante}`);
      }
    }
    
    const partidosActualizados = await Partido.findAll();
    res.json(partidosActualizados);
    
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar partidos", error: err.message });
  }
};