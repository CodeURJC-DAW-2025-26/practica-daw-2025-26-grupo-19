import { Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router";

export default function Footer() {
    return (
        <footer className="bg-dark text-light py-4 mt-auto">
            <Container>
                <Row>
                    <Col md={4} className="mb-3 mb-md-0">
                        <h5 className="text-success mb-3">⚽ Fútbol Manager</h5>
                        <p className="text-secondary" style={{ fontSize: "0.9rem" }}>
                            Tu plataforma ideal para gestionar equipos, jugadores y torneos de fútbol. 
                        </p>
                    </Col>
                    
                    <Col md={4} className="mb-3 mb-md-0">
                        <h5 className="mb-3">Enlaces Rápidos</h5>
                        <ul className="list-unstyled">
                            <li className="mb-2">
                                <Link to="/" className="text-secondary text-decoration-none custom-link">Inicio</Link>
                            </li>
                            <li className="mb-2">
                                <Link to="/torneos" className="text-secondary text-decoration-none custom-link">Torneos</Link>
                            </li>
                        </ul>
                    </Col>
                    
                    <Col md={4}>
                        <h5 className="mb-3">Contacto</h5>
                        <ul className="list-unstyled text-secondary" style={{ fontSize: "0.9rem" }}>
                            <li className="mb-1">📧 info@futbolmanager.urjc.es</li>
                            <li className="mb-1">🏫 Universidad Rey Juan Carlos</li>
                            <li className="mb-1">📍 Móstoles, Madrid</li>
                        </ul>
                    </Col>
                </Row>
                
                <hr className="border-secondary my-3" />
                
                <div className="text-center text-secondary" style={{ fontSize: "0.85rem" }}>
                    &copy; {new Date().getFullYear()} FútbolManager - Práctica DAW Grupo 19. Todos los derechos reservados.
                </div>
            </Container>
        </footer>
    );
}