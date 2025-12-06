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

// ===== GALLERIA IMMAGINI CON FRECCE =====

const modal = document.getElementById("imageModal");
const modalImg = document.getElementById("modalImg");
const closeBtn = document.querySelector(".close-modal");
const leftArrow = document.querySelector(".nav-arrow.left");
const rightArrow = document.querySelector(".nav-arrow.right");

const images = document.querySelectorAll("#preview img");
let currentIndex = 0;

// Apertura immagine
images.forEach((img, index) => {
    img.addEventListener("click", () => {
        modal.style.display = "block";
        modalImg.src = img.src;
        currentIndex = index;
    });
});

// Chiusura
closeBtn.onclick = () => {
    modal.style.display = "none";
};

// Click fuori immagine per chiudere
modal.onclick = (e) => {
    if (e.target === modal) {
        modal.style.display = "none";
    }
};

// Freccia DESTRA
rightArrow.onclick = () => {
    currentIndex++;
    if (currentIndex >= images.length) {
        currentIndex = 0;
    }
    modalImg.src = images[currentIndex].src;
};

// Freccia SINISTRA
leftArrow.onclick = () => {
    currentIndex--;
    if (currentIndex < 0) {
        currentIndex = images.length - 1;
    }
    modalImg.src = images[currentIndex].src;
};

