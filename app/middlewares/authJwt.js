import jwt from "jsonwebtoken";

// Clave secreta que coincide con tu login
const SECRET = "your_jwt_secret_key";

// Verifica si el token es válido y adjunta el usuario decodificado
export const verificarToken = (req, res, next) => {
  console.log("Header recibido:", req.headers["authorization"]);
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];

  if (!token) {
    return res.status(403).json({ message: "Token requerido" });
  }

  jwt.verify(token, SECRET, (err, decoded) => {
    if (err) {
      return res.status(401).json({ message: "Token inválido o expirado" });
    } 

    // Decodificado: { id_usuario, rol, ... }
    req.user = decoded;
    req.userId = decoded.id_usuario;
    next();
  });
};

// Solo permite acceso a administradores
export const esAdmin = (req, res, next) => {
  if (req.user.rol !== "Administrador" && req.user.rol !== "admin") {
    return res.status(403).json({ message: "Solo administradores pueden realizar esta acción" });
  }
  next();
};

// Solo permite acceso a vendedores
export const esVendedor = (req, res, next) => {
  if (req.user.rol !== "Vendedor" && req.user.rol !== "vendedor") {
    return res.status(403).json({ message: "Solo vendedores pueden realizar esta acción" });
  }
  next();
};
