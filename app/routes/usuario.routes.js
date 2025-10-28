import express from "express";
import { crearUsuario, listarUsuarios, actualizarUsuario,obtenerUsuarioPorId } from "../controllers/usuario.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.post("/", verificarToken, esAdmin, crearUsuario);
router.get("/", verificarToken, esAdmin, listarUsuarios);
router.get("/:id_usuario", verificarToken, esAdmin, obtenerUsuarioPorId);
router.put("/:id_usuario", verificarToken, esAdmin, actualizarUsuario);

export default router;

