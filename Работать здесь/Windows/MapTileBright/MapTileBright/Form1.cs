using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace MapTileBright
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }


        private Bitmap bmpSourceFile;

        private void numericUpDown1_ValueChanged(object sender, EventArgs e)
        {
            
            pictureBox2.Image = AdjustBrightness(bmpSourceFile, (Int32)(numericUpDown1.Value));
            
        }

        public static Bitmap AdjustBrightness(Bitmap Image, int Value)
        {

            System.Drawing.Bitmap TempBitmap = Image;
            float FinalValue = (float)Value / 255.0f;
            System.Drawing.Bitmap NewBitmap = new System.Drawing.Bitmap(TempBitmap.Width, TempBitmap.Height);
            System.Drawing.Graphics NewGraphics = System.Drawing.Graphics.FromImage(NewBitmap);
            float[][] FloatColorMatrix ={
                     new float[] {1, 0, 0, 0, 0},
                     new float[] {0, 1, 0, 0, 0},
                     new float[] {0, 0, 1, 0, 0},
                     new float[] {0, 0, 0, 1, 0},
                     new float[] {FinalValue, FinalValue, FinalValue, 1, 1}
                 };

            System.Drawing.Imaging.ColorMatrix NewColorMatrix = new System.Drawing.Imaging.ColorMatrix(FloatColorMatrix);
            System.Drawing.Imaging.ImageAttributes Attributes = new System.Drawing.Imaging.ImageAttributes();
            Attributes.SetColorMatrix(NewColorMatrix);
            NewGraphics.DrawImage(TempBitmap, new System.Drawing.Rectangle(0, 0, TempBitmap.Width, TempBitmap.Height), 0, 0, TempBitmap.Width, TempBitmap.Height, System.Drawing.GraphicsUnit.Pixel, Attributes);
            Attributes.Dispose();
            NewGraphics.Dispose();
            return NewBitmap;
        }

        private void openImageToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                OpenFileDialog ofd = new OpenFileDialog();
                ofd.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyComputer);
                if (ofd.ShowDialog() != DialogResult.OK)
                    return;
                bmpSourceFile = (Bitmap) Bitmap.FromFile(ofd.FileName);
                pictureBox1.Image = bmpSourceFile;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Exception", ex.ToString());
            }

        }

        private void processAllJpegImagesInFolderToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.toolStripStatusLabel1.Text = "Batch processing images";
            //show message box with warning
            DialogResult dr = MessageBox.Show("All images in selected folders will be overwritten. You need a backup copy for case when operation failed.", "Warning!", MessageBoxButtons.OKCancel, MessageBoxIcon.Asterisk, MessageBoxDefaultButton.Button2);
            if (dr == DialogResult.Cancel) return;
            //specify root folder for images
            FolderBrowserDialog fbd = new FolderBrowserDialog();
            fbd.RootFolder = Environment.SpecialFolder.MyComputer;
            fbd.Description = "Specify folder with images to processing";
            if (fbd.ShowDialog() != DialogResult.OK) return;
            //get files
            String[] files = Directory.GetFiles(fbd.SelectedPath, "*.jpg", SearchOption.AllDirectories);
            //String[] files2 = Directory.GetFiles(fbd.SelectedPath, "*.JPG");
            Bitmap bmpSrc, bmpDst;
            int brightness = (int)this.numericUpDown1.Value;
            int cnt = 0;
            try
            {

                foreach (String s in files)
                {
                    
                    bmpSrc = (Bitmap)Bitmap.FromFile(s);
                    bmpDst = AdjustBrightness(bmpSrc, brightness);
                    bmpSrc.Dispose();
                    File.Delete(s); //delete source file from folder
                    bmpDst.Save(s, System.Drawing.Imaging.ImageFormat.Jpeg);
                    bmpDst.Dispose();
                    cnt++;
                    this.toolStripStatusLabel1.Text = String.Format("{0} from {1} files processed", cnt, files.Length);
                    Application.DoEvents();
                }
                MessageBox.Show("All images processed");
            }
            catch (Exception ex)
            {
                MessageBox.Show("Process will be aborted. Not all image processed.\n You can restore files from backup and try again.\n" + ex.ToString());
            }
            return;
        }
    }
}