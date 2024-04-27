window.onload = function() {
    TagCanvas.textFont = 'Impact,"Arial Black",sans-serif';
    TagCanvas.textColour = '#694198';
    TagCanvas.textHeight = 25;
    TagCanvas.outlineColour = '#f6f';
    TagCanvas.outlineThickness = 3;
    TagCanvas.outlineOffset = 5;
    TagCanvas.outlineMethod = 'outline';
    TagCanvas.maxSpeed = 0.06;
    TagCanvas.minBrightness = 0.2;
    TagCanvas.depth = 0.95;
    TagCanvas.pulsateTo = 0.2;
    TagCanvas.pulsateTime = 0.75;
    TagCanvas.decel = 0.9;
    TagCanvas.reverse = true;
    TagCanvas.shadow = '#336';
    TagCanvas.shadowBlur = 3;
    TagCanvas.shadowOffset = [1,1];
    TagCanvas.wheelZoom = false;
    TagCanvas.fadeIn = 800;
    try {
        TagCanvas.Start('tagcanvas2','ptTags', {shape:'vcylinder'});
    } catch(e) {
    }
};
function DblHelix(n, rx, ry, rz) {
    var a = Math.PI / n, i, j, p = [], z = rz * 2 / n;
    for(i = 0; i < n; ++i) {
        j = a * i;
        if(i % 2)
            j += Math.PI;
        p.push([rx * Math.cos(j), rz - z * i, ry * Math.sin(j)]);
    }
    return p;
}
//]]>