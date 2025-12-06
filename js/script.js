/* =========================
   BURGER MENU MOBILE
========================= */
const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");

menuToggle.addEventListener("click", () => {
    sidebar.classList.toggle("aperta");
});

// Chiude la sidebar quando si clicca su un link (solo mobile)
document.querySelectorAll(".menu a").forEach(link => {
    link.addEventListener("click", () => {
        sidebar.classList.remove("aperta");
    });
});


/* =========================
   ANIMAZIONE A SCOMPARSA
   (SI RIPETE OGNI VOLTA)
========================= */
const sezioni = document.querySelectorAll(".sezione");

const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add("attiva");     
        } else {
            entry.target.classList.remove("attiva"); 
        }
    });
}, {
    threshold: 0.2
});

sezioni.forEach(sezione => observer.observe(sezione));


/* =========================
   BOTTONE TORNA SU
========================= */
const scrollTopBtn = document.getElementById("scrollTopBtn");

scrollTopBtn.addEventListener("click", () => {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
});

// MODALE IMMAGINI ANTEPRIMA
const modal = document.getElementById("imageModal");
const modalImg = document.getElementById("modalImg");
const closeBtn = document.querySelector(".close-modal");

document.querySelectorAll("#preview img").forEach(img => {
    img.addEventListener("click", () => {
        modal.style.display = "block";
        modalImg.src = img.src;
    });
});

closeBtn.onclick = () => {
    modal.style.display = "none";
};

// Chiude cliccando fuori dall'immagine
modal.onclick = (e) => {
    if (e.target === modal) {
        modal.style.display = "none";
    }
};


