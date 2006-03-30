function popUP(mypage, myname, w, h, scroll, titlebar)
{

    var winl = (screen.width - w) / 2;
    var wint = (screen.height - h) / 2;
    winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars='+scroll+',resizable'
    win = window.open(mypage, myname, winprops)
    if (parseInt(navigator.appVersion) >= 4) {
        win.window.focus();
    }
}

function processSubmit() {
//    popUP('/dropload?bookmarkablePage=status', 460, 262, false, false);
    window.frames['statusFrame'].location.href='/dropload?bookmarkablePage=status';
    window.focus();
    document.inputForm.submit();

    return true;
}
